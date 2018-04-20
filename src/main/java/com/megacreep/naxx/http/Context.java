package com.megacreep.naxx.http;

import com.megacreep.naxx.api.Controller;
import com.megacreep.naxx.api.Request;
import java.lang.reflect.*;
import java.util.HashMap;

public class Context {

    private static HashMap<String, Method> uriMapping = new HashMap<>();
    private static HashMap<String, Object> uriMapping2 = new HashMap<>();

    public static void register(Object c) {
        String name = c.getClass().getSimpleName().toLowerCase();
        Controller controller = c.getClass().getAnnotation(Controller.class);
        if (controller != null) {
            name = controller.value();
        }
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        Method[] methods = c.getClass().getDeclaredMethods();
        for (Method m : methods) {
            String mName = m.getName();
            Request request = m.getAnnotation(Request.class);
            if (request != null) {
                mName = request.value();
            }
            String path;
            if (name.endsWith("/") && mName.startsWith("/")) {
                mName = mName.substring(1);
                path = name + mName;
            } else if (!(name.endsWith("/")) && (!mName.startsWith("/"))) {
                path = name + "/" + mName;
            } else {
                path = name + mName;
            }
            uriMapping2.put(path, c);
            uriMapping.put(path, m);
            System.out.println("uriMapping " + uriMapping);
            System.out.println("uriMapping2 " + uriMapping2);
        }
    }

    public static Object invoke(HttpRequest req) {
        try {
            System.out.println("invoke path= " + req.getUri());
            return uriMapping.get(req.getUri()).invoke(uriMapping2.get(req.getUri()), req);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return "ACCESS FORBIDDEN";
        } catch (NullPointerException npe) {
            return "METHOD NOT FOUND";
        } catch (Exception e) {
            return "INTERNAL ERROR";
        }
    }
}
