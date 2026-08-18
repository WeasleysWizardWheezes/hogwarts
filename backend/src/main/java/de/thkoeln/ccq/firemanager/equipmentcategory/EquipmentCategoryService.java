package de.thkoeln.ccq.firemanager.equipmentcategory;

import de.thkoeln.ccq.firemanager.equipmentcategory.exception.EquipmentCategoryConflictException;
import de.thkoeln.ccq.firemanager.equipmentcategory.exception.EquipmentCategoryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class EquipmentCategoryService {

    private final EquipmentCategoryRepository equipmentCategoryRepository;

    public EquipmentCategoryService(EquipmentCategoryRepository equipmentCategoryRepository) {
        this.equipmentCategoryRepository = equipmentCategoryRepository;
    }

    public EquipmentCategory create(String name, String description) {
        if (this.equipmentCategoryRepository.existsByNameAndArchivedFalse(name)) {
            throw new EquipmentCategoryConflictException(
                    "EquipmentCategory with name '" + name + "' already exists"
            );
        }
        EquipmentCategory category = new EquipmentCategory(name, description);
        return this.equipmentCategoryRepository.save(category);
    }

    public Page<EquipmentCategory> getAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        String searchParam = (search != null && !search.isBlank()) ? search : null;
        return this.equipmentCategoryRepository.findAllWithSearch(searchParam, pageable);
    }

    public EquipmentCategory getById(UUID categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId must not be null");
        }
        return this.equipmentCategoryRepository.findById(categoryId)
                .filter(ec -> !ec.isArchived())
                .orElseThrow(() -> new EquipmentCategoryNotFoundException(categoryId));
    }

    public EquipmentCategory update(UUID categoryId, String name, String description) {
        EquipmentCategory category = getById(categoryId);

        if (name != null && !name.isBlank()) {
            boolean conflict = this.equipmentCategoryRepository
                    .existsByNameAndIdNotAndArchivedFalse(name, categoryId);
            if (conflict) {
                throw new EquipmentCategoryConflictException(
                        "EquipmentCategory with name '" + name + "' already exists"
                );
            }
            category.setName(name);
        }
        category.setDescription(description);
        category.setUpdatedAt(OffsetDateTime.now());

        return this.equipmentCategoryRepository.save(category);
    }

    public void deleteById(UUID categoryId) {
        EquipmentCategory category = getById(categoryId);
        category.setArchived(true);
        category.setUpdatedAt(OffsetDateTime.now());
        this.equipmentCategoryRepository.save(category);
    }
}
