package models;

import viewmodels.GameViewModel;
import java.awt.Point;

public class Net {
    public enum State {
        HIDDEN,
        CASTING,
        REELING
    }

    private State currentState;
    private Point position;
    private Point targetPosition;
    private Ball carriedBall;
    private final int speed = 25;

    public Net() {
        this.currentState = State.HIDDEN;
    }

    public void cast(Point playerPosition, Ball target) {
        if (this.currentState == State.HIDDEN) {
            this.currentState = State.CASTING;
            this.carriedBall = target;
            this.position = new Point(playerPosition);
            this.targetPosition = target.getCenter();
        }
    }

    public void update() {
        if (currentState == State.CASTING) {
            updateCasting();
        } else if (currentState == State.REELING) {
            updateReeling();
        }
    }

    private void updateCasting() {
        if (position.distance(targetPosition) < speed) {
            this.currentState = State.REELING;
            this.carriedBall.setActive(false);
            this.targetPosition = new Point(
                    GameViewModel.ROCKET_AREA.x + GameViewModel.ROCKET_AREA.width / 2,
                    GameViewModel.ROCKET_AREA.y + GameViewModel.ROCKET_AREA.height / 2
            );
        } else {
            moveTowardsTarget();
        }
    }

    private void updateReeling() {
        if (position.distance(targetPosition) < speed) {
            this.currentState = State.HIDDEN;
        } else {
            moveTowardsTarget();
            carriedBall.setX(position.x - Ball.BALL_SIZE / 2);
            carriedBall.setY(position.y - Ball.BALL_SIZE / 2);
        }
    }

    private void moveTowardsTarget() {
        double dx = targetPosition.x - position.x;
        double dy = targetPosition.y - position.y;
        double distance = position.distance(targetPosition);
        position.x += (int) (dx / distance * speed);
        position.y += (int) (dy / distance * speed);
    }

    public void reset() {
        this.carriedBall = null;
    }

    public State getState() { return currentState; }
    public Point getPosition() { return position; }
    public Ball getCarriedBall() { return carriedBall; }
}