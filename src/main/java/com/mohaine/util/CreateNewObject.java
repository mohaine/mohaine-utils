package com.mohaine.util;

import java.util.Set;

public record CreateNewObject<T>(T obj, Set<String> unhandledNames) {
}