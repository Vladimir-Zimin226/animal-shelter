package pro.sky.animal_shelter.chatStates;

public class ChatStateForBackButton {
    private String currentMenu;
    private String previousMenu;

    public ChatStateForBackButton() {
    }

    public ChatStateForBackButton(String currentMenu, String previousMenu) {
        this.currentMenu = currentMenu;
        this.previousMenu = previousMenu;
    }

    public String getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(String currentMenu, String previousMenu) {
        this.currentMenu = currentMenu;
        this.previousMenu = previousMenu;
    }

    public String getPreviousMenu() {
        return previousMenu;
    }
}
