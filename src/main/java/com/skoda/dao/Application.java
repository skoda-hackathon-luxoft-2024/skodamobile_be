package com.skoda.dao;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "applications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Application {
    @Id
    private ObjectId id;

    @DBRef
    @NonNull
    private LicenseDetail licence;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @Nullable
    private String field1;

    @Nullable
    private String field2;

    @Nullable
    private String field3;
}
