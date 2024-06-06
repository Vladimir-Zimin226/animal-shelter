package pro.sky.animal_shelter.listener;

public class ChatStateForBackButton {
    private String currentMenu;
    private String previousMenu;


    public ChatStateForBackButton(String currentStep,String previousMenu) {
        this.currentMenu = currentStep;
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
