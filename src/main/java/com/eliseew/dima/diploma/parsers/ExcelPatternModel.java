package com.eliseew.dima.diploma.parsers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExcelPatternModel(String description, List<CellCoordinate> coordinates, String reportStructure) {
    @JsonCreator
    public ExcelPatternModel(
            @JsonProperty("description") String description,
            @JsonProperty("coordinates") List<CellCoordinate> coordinates,
            @JsonProperty("reportStructure") String reportStructure) {
        this.description = description;
        this.coordinates = coordinates;
        this.reportStructure = reportStructure;
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