package de.thkoeln.ccq.firemanager.equipmentcategory;

import de.thkoeln.ccq.firemanager.generated.api.EquipmentCategoriesApi;
import de.thkoeln.ccq.firemanager.generated.model.CreateEquipmentCategoryRequest;
import de.thkoeln.ccq.firemanager.generated.model.EquipmentCategoryResponse;
import de.thkoeln.ccq.firemanager.generated.model.ListEquipmentCategories200Response;
import de.thkoeln.ccq.firemanager.generated.model.PaginationMeta;
import de.thkoeln.ccq.firemanager.generated.model.UpdateEquipmentCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class EquipmentCategoryController implements EquipmentCategoriesApi {

    private final EquipmentCategoryService equipmentCategoryService;

    public EquipmentCategoryController(EquipmentCategoryService equipmentCategoryService) {
        this.equipmentCategoryService = equipmentCategoryService;
    }

    @Override
    public ResponseEntity<EquipmentCategoryResponse> createEquipmentCategory(
            CreateEquipmentCategoryRequest createEquipmentCategoryRequest
    ) {
        EquipmentCategory category = this.equipmentCategoryService.create(
                createEquipmentCategoryRequest.getName(),
                createEquipmentCategoryRequest.getDescription()
        );
        URI location = URI.create("/api/v1/equipment-categories/" + category.getId());
        return ResponseEntity.created(location).body(toResponse(category));
    }

    @Override
    public ResponseEntity<Void> deleteEquipmentCategory(UUID categoryId) {
        this.equipmentCategoryService.deleteById(categoryId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EquipmentCategoryResponse> getEquipmentCategory(UUID categoryId) {
        EquipmentCategory category = this.equipmentCategoryService.getById(categoryId);
        return ResponseEntity.ok(toResponse(category));
    }

    @Override
    public ResponseEntity<ListEquipmentCategories200Response> listEquipmentCategories(
            Integer page,
            Integer size,
            String search
    ) {
        Page<EquipmentCategory> categoryPage =
                this.equipmentCategoryService.getAll(page, size, search);

        List<EquipmentCategoryResponse> data = categoryPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        PaginationMeta paginationMeta = new PaginationMeta()
                .page(categoryPage.getNumber())
                .size(categoryPage.getSize())
                .totalElements((int) categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages());

        ListEquipmentCategories200Response response = new ListEquipmentCategories200Response()
                .data(data)
                .page(paginationMeta);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<EquipmentCategoryResponse> updateEquipmentCategory(
            UUID categoryId,
            UpdateEquipmentCategoryRequest updateEquipmentCategoryRequest
    ) {
        EquipmentCategory category = this.equipmentCategoryService.update(
                categoryId,
                updateEquipmentCategoryRequest.getName(),
                updateEquipmentCategoryRequest.getDescription()
        );
        return ResponseEntity.ok(toResponse(category));
    }

    private EquipmentCategoryResponse toResponse(EquipmentCategory category) {
        return new EquipmentCategoryResponse()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt());
    }
}
