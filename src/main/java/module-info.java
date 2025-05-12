module com.eliseew.dima.diploma {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.apache.poi.scratchpad;
    requires org.apache.poi.ooxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens com.eliseew.dima.diploma to javafx.fxml;
    exports com.eliseew.dima.diploma;
    exports com.eliseew.dima.diploma.parsers;
    opens com.eliseew.dima.diploma.parsers to javafx.fxml;
    exports com.eliseew.dima.diploma.utils;
    opens com.eliseew.dima.diploma.utils to javafx.fxml;
}