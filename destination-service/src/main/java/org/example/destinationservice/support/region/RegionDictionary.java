package org.example.destinationservice.support.region;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class RegionDictionary {

    private final ObjectMapper objectMapper;

    private final Resource regionDictionaryResource;

    private Map<String, RegionItem> regionMap = Collections.emptyMap();

    public RegionDictionary(
            ObjectMapper objectMapper,
            @Value("${region.dictionary.location}") Resource regionDictionaryResource) {
        this.objectMapper = objectMapper;
        this.regionDictionaryResource = regionDictionaryResource;
    }

    @PostConstruct
    public void load() {
        try (InputStream inputStream = regionDictionaryResource.getInputStream()) {
            List<RegionItem> regionItems = objectMapper.readValue(
                    inputStream, new TypeReference<List<RegionItem>>() {
                    });

            Map<String, RegionItem> loadedRegionMap = new LinkedHashMap<>();
            for (RegionItem regionItem : regionItems) {
                collectRegionItems(regionItem, loadedRegionMap);
            }
            this.regionMap = Collections.unmodifiableMap(loadedRegionMap);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load region dictionary.", exception);
        }
    }

    public Optional<RegionItem> findByCode(String code) {
        return Optional.ofNullable(regionMap.get(code));
    }

    public Optional<String> findNameByCode(String code) {
        return findByCode(code).map(RegionItem::getName);
    }

    public boolean containsCode(String code) {
        return regionMap.containsKey(code);
    }

    private void collectRegionItems(RegionItem regionItem, Map<String, RegionItem> loadedRegionMap) {
        loadedRegionMap.put(regionItem.getCode(), RegionItem.builder()
                .code(regionItem.getCode())
                .name(regionItem.getName())
                .build());

        List<RegionItem> children = regionItem.getChildren();
        if (children == null || children.isEmpty()) {
            return;
        }

        for (RegionItem child : children) {
            collectRegionItems(child, loadedRegionMap);
        }
    }
}
