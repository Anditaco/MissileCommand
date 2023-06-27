public class MissileCommand_FixedComponent extends MissileCommand_GameComponent{
    @Override
    public void update(long time) {

    }

    enum State{
        ALIVE,
        DEAD
    }

    State state;

    public void destroy(){
        state = State.DEAD;
        alive = false;
    }

    public void revive(){
        state = State.ALIVE;
        alive = true;
    }
}
