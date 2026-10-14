package com.gamezone.ui;

/**
 * One option of the main menu: the label shown to the user and the action run
 * when the user chooses it. {@link ConsoleMenu} numbers and runs the options it
 * receives, so a new module is added by providing one more option, without
 * modifying the main menu.
 */
public interface MenuOption {

    /**
     * Returns the label shown in the main menu, in Spanish.
     *
     * @return the option label
     */
    String getLabel();

    /**
     * Runs the option until the user goes back to the main menu.
     */
    void run();
}
