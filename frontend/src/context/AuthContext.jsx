import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    try {
      const stored = localStorage.getItem('osm_user')
      const token = localStorage.getItem('osm_token')
      if (stored && token) {
        setUser(JSON.parse(stored))
      }
    } catch {
      localStorage.removeItem('osm_user')
      localStorage.removeItem('osm_token')
    } finally {
      setLoading(false)
    }
  }, [])

  const login = (userData, token) => {
    localStorage.setItem('osm_token', token)
    localStorage.setItem('osm_user', JSON.stringify(userData))
    setUser(userData)
  }

  const logout = () => {
    localStorage.removeItem('osm_token')
    localStorage.removeItem('osm_user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
