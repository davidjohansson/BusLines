import type {AppProps} from 'next/app'
import {ThemeProvider} from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Head from 'next/head';

import theme from '../styles/theme';

export default function App({Component, pageProps}: AppProps) {
  return <>
    <Head>
      <meta name="viewport" content="initial-scale=1, width=device-width"/>
      <link rel="shortcut icon" href="/favicon.png"/>
    </Head>
    <ThemeProvider theme={theme}>
      <CssBaseline/>
      <Component {...pageProps} />
    </ThemeProvider>
  </>
}
