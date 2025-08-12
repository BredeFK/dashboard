import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import '@radix-ui/themes/styles.css';
import {Theme} from '@radix-ui/themes';

type AccentColor = NonNullable<React.ComponentProps<typeof Theme>['accentColor']>;
const accentColors: Readonly<AccentColor[]> = [
    'gray', 'gold', 'bronze', 'brown', 'yellow', 'amber', 'orange', 'tomato',
    'red', 'ruby', 'crimson', 'pink', 'plum', 'purple', 'violet', 'iris',
    'indigo', 'blue', 'cyan', 'teal', 'jade', 'green', 'grass', 'lime', 'mint', 'sky'
];
const color: string | null = new URLSearchParams(window.location.search).get('color');
let accentColor: AccentColor = (color && accentColors.includes(color as AccentColor))
    ? color as AccentColor
    : 'sky';

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);
root.render(
    <React.StrictMode>
        <Theme appearance='dark' accentColor={accentColor} grayColor='auto' radius='large'>
            <App/>
        </Theme>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example, reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
