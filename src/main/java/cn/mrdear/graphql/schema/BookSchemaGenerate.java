package cn.mrdear.graphql.schema;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.mrdear.graphql.fetcher.GraphQLDataFetchers;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.IOException;
import java.net.URL;

/**
 * @author Quding Ding
 * @since 2019-04-08
 */
@Component
public class BookSchemaGenerate implements SchemaGenerate {

    @Autowired
    private GraphQLDataFetchers graphQLDataFetchers;

    @Override
    public GraphQLSchema buildSchema() throws IOException {
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        // schema解释器
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        // source fetcher信息
        RuntimeWiring runtimeWiring = buildWiring();

        // 绑定两者
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }



    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
            .type(newTypeWiring("Query") // 注册dataFetcher
                .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher()))
            .type(newTypeWiring("Book")
                .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher())
                .dataFetcher("pageCount",graphQLDataFetchers.getPageCountDataFetcher()))
            .build();
    }

}
