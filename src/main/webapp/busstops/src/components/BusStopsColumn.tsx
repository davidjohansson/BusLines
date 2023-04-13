import React from 'react'
import {Grid, List, ListItem, Typography} from "@mui/material";
import {styled} from "@mui/system";

interface IBusStopColumnProps {
  stops: String[]
  columnKey: String
}

export default function BusStopsColumn({stops, columnKey}: IBusStopColumnProps) {
  const StyledDiv = styled('div')(({theme}) => ({
    backgroundColor: theme.palette.background?.paper,
    borderRadius: 10
  }));

  return (
      <Grid item xs={4} >
        <StyledDiv>
          <List dense>
            {stops.map((stop, stopIndex) => {
              return (
                  <ListItem key={`${columnKey}_${stopIndex}`}>
                    <Typography variant={"body2"}>
                      {stop}
                    </Typography>
                  </ListItem>
              )
            })}
          </List>
        </StyledDiv>
      </Grid>
  )
}