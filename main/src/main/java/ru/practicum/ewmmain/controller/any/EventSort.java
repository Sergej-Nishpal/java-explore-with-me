package ru.practicum.ewmmain.controller.any;

public enum EventSort {
    EVENT_DATE {
        @Override
        public String toString() {
            return "eventDate";
        }
    }, VIEWS {
        @Override
        public String toString() {
            return "views";
        }
    }, DISTANCE_KM {
        @Override
        public String toString() {
            return "distanceKm";
        }
    }
}