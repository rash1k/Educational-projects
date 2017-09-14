package ua.example.rash1k.cannongame;

public class Target extends GameElement {

    private int hitReward; //Награда за попадание(Увеличение времени)


    public Target(CannonView view, int color, int hitReward, int x, int y, int width, int length, float velocity) {
        super(view, color, CannonView.BLOCKER_SOUND_ID, x, y, width, length, velocity);

        this.hitReward = hitReward;
    }

    public int getHitReward() {
        return hitReward;
    }
}
