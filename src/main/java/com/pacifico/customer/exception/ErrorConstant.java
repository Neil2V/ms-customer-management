package com.pacifico.customer.exception;

public class ErrorConstant {
    public enum Type {
        SYSTEM("Sistema");
        private final String description;
        Type(String description) { this.description = description; }
        public String getDescription() { return description; }
    }
    public enum Layer {
        SUPPORT("Soporte");
        private final String description;
        Layer(String description) { this.description = description; }
        public String getDescription() { return description; }
    }
    public enum SystemComponent {
        SUPPORT("Soporte");
        private final String description;
        SystemComponent(String description) { this.description = description; }
        public String getDescription() { return description; }
    }
    public enum Category {
        ERROR("Error");
        private final String description;
        Category(String description) { this.description = description; }
        public String getDescription() { return description; }
    }
}
