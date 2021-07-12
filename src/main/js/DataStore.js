import {action, observable} from "mobx";

export class DataStore {

    @observable
    lastWebSocketEvent = null;

    @action
    onWsEvent = (m) => {
        console.log('AAAA', m)
    }

}