import React from 'react';
import Router from './shared/Router';
import { QueryClient, QueryClientProvider } from 'react-query';
import { Provider } from 'react-redux';
import { store, persistor} from './config/configStore';
import { PersistGate } from 'redux-persist/integration/react';

const queryClient = new QueryClient();

function App() {
  return (
    <Provider store={store}>
      <QueryClientProvider client={queryClient}>
        <PersistGate persistor={persistor}>
          <Router />
        </PersistGate>
      </QueryClientProvider>
    </Provider>
  )
}

export default App