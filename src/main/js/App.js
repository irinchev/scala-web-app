import React from "react";
import Nav from "./Nav";

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            ws: null
        };
    }

    componentDidMount() {
        this.connect();
    }

    connect = () => {
        const url = new URL('/ws', window.location.href);
        let ws = new WebSocket(url.href.replace(/^http/, 'ws'));
        let that = this;
        let connectInterval;
        ws.onopen = () => {
            this.setState({ ws: ws });
            ws.send("Hi there")
            that.timeout = 250;
            clearTimeout(connectInterval);
        };
        ws.onclose = e => {
            that.timeout = that.timeout + that.timeout;
            connectInterval = setTimeout(this.check, Math.min(10000, that.timeout));
        };
        ws.onerror = err => {
            ws.close();
        };
    };

    sendMessage = () => {
        if(this.state.ws) {
            this.state.ws.send("Now is " + new Date())
        }
    }

    render() {
        return (
            <div className="w-full h-full flex flex-col">
                <Nav/>
                <div className="pt-10">
                    <button className="bg-gray-50 p-8" onClick={this.sendMessage}>Click</button>
                </div>
            </div>
        );
    }
}

export default App;
