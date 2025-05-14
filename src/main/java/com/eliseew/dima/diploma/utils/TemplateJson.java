package com.eliseew.dima.diploma.utils;

public class TemplateJson {

    public String description, trigger, regex, actionType, replacementValue, reportStructure;

    public TemplateJson(String description, String trigger,
                        String regex, String actionType, String replacementValue, String reportStructure) {

        this.description = description;
        this.trigger = trigger;
        this.regex = regex;
        if (actionType.equals("отчет")){
            this.actionType = "report";
        } else if (actionType.equals("замена")) {
            this.actionType = "replace";
        }
        this.replacementValue = replacementValue;
        this.reportStructure = reportStructure;
    }

    public TemplateJson() {
    }

    public String getDescription() {
        return description;
    }
}
