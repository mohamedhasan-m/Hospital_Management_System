package com.hospital.model;

/** Anything that can describe itself as a JSON object. */
public interface Jsonable {
    String toJson();
}
