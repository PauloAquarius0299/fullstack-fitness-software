'use client'
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
  user: string | null;        
  token: string | null;
  userId: string | null;
}

const initialState: AuthState = {
  user: typeof window !== 'undefined' ? JSON.parse(localStorage.getItem('user') || 'null') : null,
  token: typeof window !== 'undefined' ? localStorage.getItem('token') : null,
  userId: typeof window !== 'undefined' ? localStorage.getItem('userId') : null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (
      state,
      action: PayloadAction<{ user: string; token: string; userId: string }>
    ) => {
      const { user, token, userId } = action.payload;
      state.user = user;
      state.token = token;
      state.userId = userId;

      localStorage.setItem('user', JSON.stringify(user));
      localStorage.setItem('token', token);
      localStorage.setItem('userId', userId);
    },
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.userId = null;

      localStorage.removeItem('user');
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
    },
  },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;
