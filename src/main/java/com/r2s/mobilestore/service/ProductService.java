package com.r2s.mobilestore.service;


import com.r2s.mobilestore.data.dto.FilterProductDTO;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.product.ProductDTO;

import com.r2s.mobilestore.data.dto.product.ShowProductRelated;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
/**
 * @author NguyenTienDat
 */
public interface ProductService {

    ProductDTO create(ProductDTO productDTO, List<MultipartFile> fileImages);

    ProductDTO update(long id, ProductDTO productDTO, List<MultipartFile> fileImages);

    PaginationDTO findAllActiveProduct(int no, int limit);

    PaginationDTO searchProduct(String keyword, int no, int limit);

    boolean deleteById(long id);

    List<ProductDTO>  findNewProduct();

    PaginationDTO showListProductPagination(long categoryId , int no , int limit);

    ProductDTO showDetailProduct(long id);

    PaginationDTO showListProductFilter(FilterProductDTO filterProductDTO, int no, int limit);
    PaginationDTO showProductByCategoryAndManufacturer(long category_id , long manufacturer_id, int no, int limit);

    List<ShowProductRelated> showRelatedProduct(long productId, int quantity);

    PaginationDTO showActiveProductByCategory(long id, int no, int limit);

    PaginationDTO showActiveProductByManufacturer(long id, int no, int limit);
    PaginationDTO FilterProductsByPrice(BigDecimal lowerPrice, BigDecimal higherPrice, int no, int limit);

    }
