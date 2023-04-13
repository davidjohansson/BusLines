import React, {useEffect, useState} from 'react'
import DirectionsBusIcon from '@mui/icons-material/DirectionsBus';

import {
  AppBar,
  Box,
  Grid,
  Toolbar,
  Typography
} from "@mui/material";
import BusLines from "@/components/BusLines";

export default function Home() {
  const [data, setData] = useState<Map<String, String[]>>(new Map())
  const [isLoading, setLoading] = useState(false)
  useEffect(() => {
        setLoading(true)
        fetchWithType()
        .then((data) => {
          setData(data)
          setLoading(false)
        })
      }
      , [])

  async function fetchWithType(): Promise<Map<String, String[]>> {
    const response = await fetch('/stops');
    return await response.json();
  }


  if (isLoading) return <p>Loading...</p>
  if (!data) return <p>No stops data</p>

  return (
      <>
        <Box sx={{mb: 3}}>
          <AppBar>
            <Toolbar>
              <DirectionsBusIcon sx={{mr: 2}}/>
              <Typography variant="h6" component="div">
                Busslinjer
              </Typography>
            </Toolbar>
          </AppBar>
        </Box>
        <main>
          <BusLines busslines={data}/>
        </main>
      </>
  )
}
