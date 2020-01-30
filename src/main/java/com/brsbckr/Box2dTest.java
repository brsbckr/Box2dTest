package com.brsbckr;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Box2dTest extends JFrame {

    Box2dPanel box2dPanel = new Box2dPanel();

    public Box2dTest(){
        initComponents();
        this.getContentPane().add(BorderLayout.CENTER, box2dPanel);
    }

    private void initComponents(){

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        box2dPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addBoxActionPerformed(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((size.width-720)/2, (size.height-575)/2, 720, 575);
    }

    public void addBoxActionPerformed(MouseEvent e){
        box2dPanel.addBox(e.getX(), e.getY() - 40);
    }

    public static void main(String[] args){

        EventQueue.invokeLater(() -> new Box2dTest().setVisible(true));

    }

    private class Box2dPanel extends JPanel implements ActionListener {

        int width;
        int height;

        World world;

        Timer refreshTimer;

        float timeStep = 1.0f/60.0f;

        int velocityIterations = 6;
        int positionIterations = 2;

        ArrayList<Body> bodies = new ArrayList<>();

        Body groundBody;

        public Box2dPanel(){
            setBackground(Color.black);
            createWorld();
            refreshTimer = new Timer((int) (100.0 / 60.0), this);
            refreshTimer.start();
        }

        public void createWorld(){
            Vec2 gravity = new Vec2(0.0f, 10.0f);

            world = new World(gravity);

            /// ground
            BodyDef groundBodyDef = new BodyDef();
            groundBodyDef.position.set(0, 350);

            groundBody = world.createBody(groundBodyDef);

            PolygonShape groundBox = new PolygonShape();
            groundBox.setAsBox(700.0f, 10.0f);

            groundBody.createFixture(groundBox, 0.1f);


        }

        public void resetTrans(Graphics2D g){
            AffineTransform at = new AffineTransform();
            at.setToIdentity();
            g.setTransform(at);
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Dimension d = getSize();
            width = d.width;
            height = d.height;

            resetTrans(g2d);

            // draw ground
            g2d.setColor(Color.green);
            g2d.drawRect((int)groundBody.getPosition().x, (int)groundBody.getPosition().y + 40, 700, 20);

            for(Body body : bodies){
                resetTrans(g2d);
                g2d.translate(body.getWorldCenter().x, body.getWorldCenter().y);
                g2d.drawRect(0, 40, 20, 20);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            world.step(timeStep, velocityIterations, positionIterations);
            this.repaint();
        }

        public void addBox(int x, int y){
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.DYNAMIC;
            bodyDef.position.set(x, y);

            Body body = world.createBody(bodyDef);

            PolygonShape dynamicBox = new PolygonShape();
            dynamicBox.setAsBox(10.0f, 10.0f);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = dynamicBox;
            fixtureDef.density = 25.0f;
            fixtureDef.friction = 0.3f;
            fixtureDef.restitution = 0.8f;

            body.createFixture(fixtureDef);

            bodies.add(body);
        }
    }

}
