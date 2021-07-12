import React from "react";
import {render} from "react-dom";
import {Provider} from "mobx-react";
import App from "./App";
import "./index.css";
import {DataStore} from "./DataStore";

const store = new DataStore()
render(
    <Provider dataStore={store}>
        <App/>
    </Provider>,
    document.getElementById("app")
);