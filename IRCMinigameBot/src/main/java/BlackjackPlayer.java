import java.util.ArrayList;

public class BlackjackPlayer {
    private String name;
    private ArrayList<Integer> hand = new ArrayList<>();
    private boolean isStay = false;

    public BlackjackPlayer(String name) { this.name = name; }

    public String getName() { return name; }

    public void addToHand(int card) { hand.add(card); }

    public ArrayList<Integer> getHand() { return hand; }

    public void setStay(boolean stay) { isStay = stay; }

    public boolean isStay() { return isStay; }
}
