const path = require('path');
const PATHS = {
    build: path.join(__dirname, 'target', 'js')
};

module.exports = {
    entry: './src/main/js/index.js',
    output: {
        path: PATHS.build,
        filename: 'bundle.js'
    },
    devtool: "source-map",
    module: {
        rules: [
            {
                test: /\.js[x]?$/,
                loader: 'babel-loader',
                exclude: /node_modules/
            },
            {
                test: /\.(png|jpg|jpeg|gif|ico|svg|woff|eot|ttf|woff2)$/,
                use: [
                    {
                        loader: 'file-loader'
                    }
                ]
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader', 'postcss-loader']
            }
        ]
    },
    devServer: {
        contentBase: './src/main/html',
        historyApiFallback: true,
        proxy:{
            '/api/*' : {
                target: 'http://localhost:8070'
            },
            '/ws' : {
                target: "http://[::1]:9000",
                ws: true,
                secure: false,
                changeOrigin: false
            }
        }
    }
};
