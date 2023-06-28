import React from 'react'

const CommonComponent = ({ children }) => {

  return (
    <div>
        <header style={{  }}>

        </header>

        <main>
            {children}
        </main>

        <footer style={{  }}>

        </footer>
    </div>
  )
}

export default CommonComponent