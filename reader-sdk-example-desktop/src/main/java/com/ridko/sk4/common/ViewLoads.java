package com.ridko.sk4.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class ViewLoads {
    public static class ViewPane<T> {
        private Parent parent;
        private T controller;

        public Parent getParent() {
            return parent;
        }

        public void setParent(Parent parent) {
            this.parent = parent;
        }

        public T getController() {
            return controller;
        }

        public void setController(T controller) {
            this.controller = controller;
        }

        public ViewPane(Parent parent, T controller) {
            this.parent = parent;
            this.controller = controller;
        }
    }

    public static <T> ViewPane<T> load(URL url) throws IOException {
        FXMLLoader loader = new FXMLLoader(url);
        Parent parent = loader.load();
        T controller = loader.getController();
        //noinspection unchecked
        return new ViewPane(parent, controller);
    }
}
