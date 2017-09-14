package ua.example.rash1k.cannongame;

public class Blocker extends GameElement {

    private int missPenalty; //Потеря времени при попадании в блок

    public Blocker(CannonView view, int color, int missPenalty,
                   int x, int y, int width, int length, float velocity) {

        super(view, color, CannonView.BLOCKER_SOUND_ID, x, y, width, length, velocity);
        this.missPenalty = missPenalty;
    }

    //Возврат штрафа при попадании в блок
    public int getMissPenalty() {
        return missPenalty;
    }
}
