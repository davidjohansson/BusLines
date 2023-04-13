import {render, screen} from '@testing-library/react'
import Home from '../pages/index'
import '@testing-library/jest-dom'
import BusLines from "../components/BusLines";

describe('BustStopLines', () => {
  it('renders each bus line', () => {

    render(<BusLines busslines={
      {
        "1": ["1", "2"],
        "2": ["1", "2", "3"]
      }
    }/>)

    const line1 = screen.getByText("Linje 1 (2 hållplatser)");
    const line2 = screen.getByText("Linje 2 (3 hållplatser)");

    expect(line1).toBeInTheDocument()
    expect(line2).toBeInTheDocument()
  })
})