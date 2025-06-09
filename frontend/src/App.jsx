import {BrowserRouter as Router, Navigate, Route, Routes, useLocation } from 'react-router-dom'
import {Box, Button} from '@mui/material';
import { useContext, useEffect, useState } from 'react';
import { AuthContext } from 'react-oauth2-code-pkce';
import { useDispatch } from 'react-redux';
import { setCredentials } from './store/authSlice';
import ActivityFrom from './components/ActivityFrom';
import ActivityList from './components/ActivityList';
import ActivityDetail from './components/ActivityDetail';

const ActivitiesPage = () => {
  return (
  <Box component='section' sx={{p: 2, border: '1px dashed grey'}}>
    <ActivityFrom onActivitiesAdded = {() => window.location.reload()} />
    <ActivityList />
  </Box>
  );
}

function App() {

  const {token, tokenData, logIn, logOut, isAuthenticated} = useContext(AuthContext);
  const dispatch = useDispatch();
  const [authReady, setAuthReady] = useState(false);

  useEffect(()=> {
    if(token){
      dispatch(setCredentials({token, user: tokenData}));
      setAuthReady(true)
    }
  }, [token, tokenData, dispatch]);

  return (
    <Router>
      {!token ? (
        <Button
        onClick={() => {
          logIn();
        }}
        >LOGIN</Button>      
     ) : (
      <Box component='section' sx={{p: 2, border: '1px dashed grey'}}>
        <Routes>
          <Route path='/activities' element={<ActivitiesPage />} />
          {/*<Route path='/activities/:id' element={<ActivityDetail />} />*/}
          <Route path='/' element={token ? <Navigate to='/activities' replace /> : <div>Welcome to login page</div>} />
        </Routes>
      </Box>
     )}
    </Router>
  )
}

export default App
