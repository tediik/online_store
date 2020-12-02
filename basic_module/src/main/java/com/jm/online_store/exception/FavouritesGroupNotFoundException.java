package com.jm.online_store.exception;

public class FavouritesGroupNotFoundException extends RuntimeException{
        public FavouritesGroupNotFoundException() {
            super("FavouritesGroup not found");
        }
}
