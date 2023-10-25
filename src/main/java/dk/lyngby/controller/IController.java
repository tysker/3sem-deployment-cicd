package dk.lyngby.controller;

import dk.lyngby.exception.ApiException;
import io.javalin.http.Context;

public interface IController<T, D> {
    void read(Context ctx) throws ApiException;
    void readAll(Context ctx);
    void create(Context ctx);
    void update(Context ctx);
    void delete(Context ctx) throws ApiException;
    boolean validatePrimaryKey(D d);
    T validateEntity(Context ctx);

}
