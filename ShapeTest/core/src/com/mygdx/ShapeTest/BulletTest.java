package com.mygdx.ShapeTest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

public class BulletTest implements ApplicationListener, GestureDetector.GestureListener, InputProcessor {

    final static short GROUND_FLAG = 1 << 8;
    final static short OBJECT_FLAG = 1 << 9;
    final static short ALL_FLAG = -1;

    Array<GameObject> instances;
    ArrayMap<String, GameObject.Constructor> constructors;

    PerspectiveCamera cam;
    CameraInputController camController;
    ModelBatch modelBatch;

    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;

    Environment environment;

    Model model;

    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;

    float spawnTimer;

    btBroadphaseInterface broadphase;

    btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;

    final int BOXCOUNT_X = 10;
    final int BOXCOUNT_Y = 10;
    final int BOXCOUNT_Z = 1;

    final float BOXOFFSET_X = -3.0f;
    final float BOXOFFSET_Y = 1f;
    final float BOXOFFSET_Z = 0f;


    static class MyMotionState extends btMotionState {
        Matrix4 transform;

        @Override
        public void getWorldTransform(Matrix4 worldTrans) {
            worldTrans.set(transform);
        }

        @Override
        public void setWorldTransform(Matrix4 worldTrans) {
            transform.set(worldTrans);
        }
    }

    static class GameObject extends ModelInstance implements Disposable {
        public final btRigidBody body;
        public final MyMotionState motionState;


        public GameObject(Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
            super(model, node);
            motionState = new MyMotionState();
            motionState.transform = transform;

            body = new btRigidBody(constructionInfo);
            body.setMotionState(motionState);

        }


        @Override
        public void dispose() {
            body.dispose();
            motionState.dispose();
        }

        static class Constructor implements Disposable {
            public final Model model;
            public final String node;
            public final btCollisionShape shape;

            public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
            private static Vector3 localInertia = new Vector3();

            public Constructor(Model model, String node, btCollisionShape shape, float mass) {
                this.model = model;
                this.node = node;
                this.shape = shape;
                if (mass > 0f)
                    shape.calculateLocalInertia(mass, localInertia);

                else
                    localInertia.set(0, 0, 0);
                this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
            }

            public GameObject construct() {
                return new GameObject(model, node, constructionInfo);
            }

            @Override
            public void dispose() {
                shape.dispose();
                constructionInfo.dispose();
            }


        }
    }


    @Override
    public void create() {
        Bullet.init();
        stage = new Stage();
        font = new BitmapFont();
        font.setScale(5f, 5f);
        label = new Label(" ", new Label.LabelStyle(font, Color.YELLOW));

        stage.addActor(label);
        stringBuilder = new StringBuilder();

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(20f, 20f, 20f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();


        camController = new CameraInputController(cam);
        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(new InputMultiplexer(gd, camController));


        ModelBuilder mb = new ModelBuilder();

        mb.begin();


        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createReflection(Color.GREEN), ColorAttribute.createSpecular(Color.GREEN), FloatAttribute
                .createShininess(16f)))
                .box(155f, 0.5f, 155f);
        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(1f, 1f, 1f, 10, 10);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
                .box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
                .cone(1f, 2f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN)))
                .capsule(0.5f, 2f, 10);
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
                .cylinder(1f, 2f, 1f, 10);
        mb.node().id = "mystick";
        mb.part("mystick", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)))
                .box(1f, 1f, 1f);
        mb.node().id = "bst";
        mb.part("bst", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.ORANGE)))
                .box(1f, 1f, 1f);
        //основание х, высота, основание у
        mb.node().id = "ball";
        mb.part("ball", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLACK)))
                .sphere(1f, 1f, 1f, 10, 10);
        //.sphere(0.5f, 0.5f, 0.5f, 10, 10);

        model = mb.end();

        constructors = new ArrayMap<String, GameObject.Constructor>(String.class, GameObject.Constructor.class);
        constructors.put("ground", new GameObject.Constructor(model, "ground", new btBoxShape(new Vector3(155f, 0.5f, 155f)), 0f));
        constructors.put("sphere", new GameObject.Constructor(model, "sphere", new btSphereShape(0.5f), 1f));
        constructors.put("box", new GameObject.Constructor(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f));
        constructors.put("cone", new GameObject.Constructor(model, "cone", new btConeShape(0.5f, 2f), 1f));
        constructors.put("capsule", new GameObject.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f), 1f));
        constructors.put("cylinder", new GameObject.Constructor(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f)), 1f));
        constructors.put("mystick", new GameObject.Constructor(model, "mystick", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f));
        constructors.put("bst", new GameObject.Constructor(model, "bst", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 5f));
        //     ,высота от земли,
        constructors.put("ball", new GameObject.Constructor(model, "ball", new btSphereShape(1f), 5f));
        //last -> mass

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));

        // contactListener = new MyContactListener();

        instances = new Array<GameObject>();

        GameObject object = constructors.get("ground").construct();
        object.transform.trn(0, 0, 0);
        object.body.proceedToTransform(object.transform);
        object.body.setCollisionFlags(object.body.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        instances.add(object);


        dynamicsWorld.addRigidBody(object.body);


        object.body.setContactCallbackFlag(GROUND_FLAG);
        object.body.setContactCallbackFilter(0);

        object.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        createBoxes();

    }


    public void createBoxes() {

        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    GameObject object = constructors.get("bst").construct();
                    object.transform.trn(BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z);
                    object.body.proceedToTransform(object.transform);
                    object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

                    instances.add(object);
                    dynamicsWorld.addRigidBody(object.body);

                    object.body.setContactCallbackFlag(OBJECT_FLAG);
                    object.body.setContactCallbackFilter(GROUND_FLAG);

                }

            }
        }

        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    GameObject object = constructors.get("bst").construct();
                    object.transform.trn(BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z - 11);
                    object.body.proceedToTransform(object.transform);
                    object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

                    instances.add(object);
                    dynamicsWorld.addRigidBody(object.body);

                    object.body.setContactCallbackFlag(OBJECT_FLAG);
                    object.body.setContactCallbackFilter(GROUND_FLAG);

                }

            }
        }

        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    GameObject object = constructors.get("bst").construct();
                    object.transform.trn(BOXOFFSET_X + z, BOXOFFSET_Y + y, BOXOFFSET_Z + x - 10);
                    object.body.proceedToTransform(object.transform);
                    object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

                    instances.add(object);
                    dynamicsWorld.addRigidBody(object.body);

                    object.body.setContactCallbackFlag(OBJECT_FLAG);
                    object.body.setContactCallbackFilter(GROUND_FLAG);

                }

            }
        }

        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    GameObject object = constructors.get("bst").construct();
                    object.transform.trn(BOXOFFSET_X + z + 9, BOXOFFSET_Y + y, BOXOFFSET_Z + x - 10);
                    object.body.proceedToTransform(object.transform);
                    object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

                    instances.add(object);
                    dynamicsWorld.addRigidBody(object.body);

                    object.body.setContactCallbackFlag(OBJECT_FLAG);
                    object.body.setContactCallbackFilter(GROUND_FLAG);

                }

            }
        }

        /*for (int x = 0; x < BOXCOUNT_X; x += 2) {
            GameObject object = constructors.get("cylinder").construct();
            object.transform.trn(BOXOFFSET_X + x, BOXOFFSET_Y, BOXOFFSET_Z);
            object.body.proceedToTransform(object.transform);
            object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

            instances.add(object);
            dynamicsWorld.addRigidBody(object.body);

            object.body.setContactCallbackFlag(OBJECT_FLAG);
            object.body.setContactCallbackFilter(GROUND_FLAG);
        }*/

    }
    //float angle, speed = 90f;

    @Override
    public void render() {
        final float delta = Math.min(1f / 60f, Gdx.graphics.getDeltaTime());

        /*angle = (angle + delta * speed) % 360f;
        instances.get(0).transform.setTranslation(0, MathUtils.sinDeg(angle) * 2.5f, 0f);*/


        dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 5, 1f / 60f);


        if ((spawnTimer -= delta) < 0) {
            //spawn();
            spawnTimer = 1.5f;
        }

        camController.update();

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();

        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Status: ").append(status);
        //label.setText(stringBuilder);

        stage.draw();
    }

    /*public void spawn() {
        GameObject obj = constructors.values[1 + MathUtils.random(constructors.size - 2)].construct();

        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.proceedToTransform(obj.transform);

        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(obj);

        dynamicsWorld.addRigidBody(obj.body);

        obj.body.setContactCallbackFlag(OBJECT_FLAG);
        obj.body.setContactCallbackFilter(GROUND_FLAG);
    }*/

    @Override
    public void dispose() {

        for (GameObject obj : instances)
            obj.dispose();
        instances.clear();

        for (GameObject.Constructor ctor : constructors.values())
            ctor.dispose();
        constructors.clear();

        dynamicsWorld.dispose();
        constraintSolver.dispose();

        broadphase.dispose();

        dispatcher.dispose();
        collisionConfig.dispose();

        //contactListener.dispose();


        modelBatch.dispose();
        model.dispose();
    }

    /*public void testForce(float x, float y) {
        GameObject obj = constructors.values[1 + MathUtils.random(constructors.size - 2)].construct();

        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.proceedToTransform(obj.transform);

        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(obj);

        dynamicsWorld.addRigidBody(obj.body);

        obj.body.setContactCallbackFlag(OBJECT_FLAG);
        obj.body.setContactCallbackFilter(GROUND_FLAG);
    }*/


    private void shoot(float x, float y) {
        Ray ray = cam.getPickRay(x, y);
        GameObject object = constructors.get("ball").construct();
        object.transform.trn(ray.origin.x, ray.origin.y, ray.origin.z);
        object.body.proceedToTransform(object.transform);
        object.body.setUserValue(instances.size);
        object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        object.body.applyCentralImpulse(ray.direction.scl(1000f));
        instances.add(object);

        dynamicsWorld.addRigidBody(object.body);

        object.body.setContactCallbackFlag(OBJECT_FLAG);
        object.body.setContactCallbackFilter(GROUND_FLAG);
    }


    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void resize(int width, int height) {
    }

    private String status;


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        shoot(x, y);
        status = "tap";
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }


}