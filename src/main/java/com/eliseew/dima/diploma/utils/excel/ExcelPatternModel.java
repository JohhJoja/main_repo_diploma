package com.eliseew.dima.diploma.utils.excel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExcelPatternModel(String description, List<CellCoordinate> coordinates) {
    @JsonCreator
    public ExcelPatternModel(
            @JsonProperty("description") String description,
            @JsonProperty("coordinates") List<CellCoordinate> coordinates
            ) {
        this.description = description;
        this.coordinates = coordinates;
        }

    public String reportStructure() {
        System.out.println("Сработал reportStructure в ExcelPatternModel");
        return "";
    }

    public record CellCoordinate(int row, int col) {
        @JsonCreator
        public CellCoordinate(
                @JsonProperty("row") int row,
                @JsonProperty("col") int col) {
            this.row = row;
            this.col = col;
        }
    }
}