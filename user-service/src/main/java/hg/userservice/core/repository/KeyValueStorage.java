package hg.userservice.core.repository;

import hg.userservice.core.vo.KeyType;

import java.util.Optional;

import static java.time.Duration.ofMillis;
import static java.util.Optional.ofNullable;

public interface KeyValueStorage {
    void putValue(KeyType type, String key, String value);

    Optional<String> getValue(KeyType type, String identifier);

    void deleteKey(KeyType type, String identifier);
}
