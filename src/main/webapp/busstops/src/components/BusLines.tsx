import React from 'react'
import {Accordion, AccordionDetails, AccordionSummary, Grid, Typography} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import BusStopsColumn from "@/components/BusStopsColumn";

interface IBusLinesProperties {
  busslines: Map<String, String[]>
}

export default function BusLines({busslines}: IBusLinesProperties) {

  function spliceIntoThreeChunks(arr: String[]): String[][] {
    const chunkSize = Math.ceil(arr.length / 3);
    const chunks = [];

    for (let i = 0; i < arr.length; i += chunkSize) {
      chunks.push(arr.slice(i, i + chunkSize));
    }
    return chunks;
  }

  return (
      <>
        {
          Object.entries(busslines).map(([key, value]: [String, String[]], lineIndex: number) => {
                return (
                    <Accordion key={`${lineIndex}`}>
                      <AccordionSummary
                          expandIcon={<ExpandMoreIcon/>}
                          aria-controls="panel1a-content"
                          id="panel1a-header"
                      >
                        <Typography>{`Linje ${key} (${value.length} hållplatser)`}</Typography>
                      </AccordionSummary>

                      <AccordionDetails sx={{backgroundColor: 'lightgray'}}>
                        <Grid container spacing={2}>
                          {
                            spliceIntoThreeChunks(value).map((chunk, colIndex) => {
                              return (
                                  <BusStopsColumn stops={chunk}
                                                  key={`bscg_${lineIndex}_${colIndex}`}
                                                  columnKey={`bscg_${lineIndex}_${colIndex}`}/>
                              )
                            })
                          }
                        </Grid>
                      </AccordionDetails>
                    </Accordion>
                )
              }
          )
        }
      </>
  )
}