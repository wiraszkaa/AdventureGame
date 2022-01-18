module jakubwiraszka.adventurefxmaven {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.hildan.fxgson;

    opens jakubwiraszka;
    opens jakubwiraszka.dialogs;
    opens jakubwiraszka.gamefiles;
    opens jakubwiraszka.visuals;

    exports jakubwiraszka;
    exports jakubwiraszka.gamefiles;
    exports jakubwiraszka.observable;
    exports jakubwiraszka.visuals;
    exports jakubwiraszka.items;
    exports jakubwiraszka.fight;
}