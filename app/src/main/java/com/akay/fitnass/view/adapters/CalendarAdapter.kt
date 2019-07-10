package com.akay.fitnass.view.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.akay.fitnass.R
import com.akay.fitnass.data.model.Flight
import com.akay.fitnass.view.custom.calendar.inflate
import org.threeten.bp.format.DateTimeFormatter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.calendar_item_event.view.*

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    val flights = mutableListOf<Flight>()

    private val formatter = DateTimeFormatter.ofPattern("EEE'\n'dd MMM'\n'HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(parent.inflate(R.layout.calendar_item_event))
    }

    override fun onBindViewHolder(viewHolder: CalendarViewHolder, position: Int) {
        viewHolder.bind(flights[position])
    }

    override fun getItemCount(): Int = flights.size

    inner class CalendarViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(flight: Flight) {
            containerView.itemFlightDateText.text = formatter.format(flight.time)
            containerView.itemFlightDateText.setBackgroundColor(ContextCompat.getColor(containerView.context, flight.color))

            containerView.itemDepartureAirportCodeText.text = flight.departure.code
            containerView.itemDepartureAirportCityText.text = flight.departure.city

            containerView.itemDestinationAirportCodeText.text = flight.destination.code
            containerView.itemDestinationAirportCityText.text = flight.destination.city
        }
    }
}