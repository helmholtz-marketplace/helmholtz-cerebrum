package de.helmholtz.marketplace.cerebrum.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public final class CerebrumControllerUtilities
{
    private CerebrumControllerUtilities()
    { }

    public static <T> T applyPatch(
            JsonPatch patch, T target, Class<T> clazz) throws JsonPatchException, JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(target, JsonNode.class));
        return objectMapper.treeToValue(patched, clazz);
    }

    public static List<Sort.Order> getOrders(List<String> sorts)
    {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sort: sorts) {
            if (sort.contains(".")) {
                String[] order = sort.split("\\.");
                orders.add(new Sort.Order(
                        order[1].equals("asc") ?
                                Sort.Direction.ASC :
                                Sort.Direction.DESC, order[0])
                );
            } else {
                orders.add(new Sort.Order(Sort.Direction.ASC, sort));
            }
        }
        return orders;
    }
}
