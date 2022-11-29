import Core.APIRequester;
import UI.TextUI;
import UI.UI;

public class Main {
    public static void main(String[] args) {
        UI ui = new TextUI();
        APIRequester requester = new APIRequester(ui);
        requester.execute();
    }
}
