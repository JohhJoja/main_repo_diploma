module com.eliseew.dima.diploma {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.eliseew.dima.diploma to javafx.fxml;
    exports com.eliseew.dima.diploma;
}