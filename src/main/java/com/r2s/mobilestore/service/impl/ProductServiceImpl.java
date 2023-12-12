package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.common.enumeration.ENum;
import com.r2s.mobilestore.data.dto.*;
import com.r2s.mobilestore.data.dto.product.ProductDTO;
import com.r2s.mobilestore.data.dto.product.ShowProductDTO;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.product.ShowProductRelated;
import com.r2s.mobilestore.data.entity.Categories;
import com.r2s.mobilestore.data.entity.Manufacturer;
import com.r2s.mobilestore.data.entity.Product;
import com.r2s.mobilestore.data.entity.Review;
import com.r2s.mobilestore.data.mapper.CategoriesMapper;
import com.r2s.mobilestore.data.mapper.ManufacturerMapper;
import com.r2s.mobilestore.data.mapper.ProductMapper;
import com.r2s.mobilestore.data.repository.OrdersRepository;
import com.r2s.mobilestore.data.repository.ProductRepository;
import com.r2s.mobilestore.exception.*;
import com.r2s.mobilestore.data.mapper.*;
import com.r2s.mobilestore.data.repository.*;
import com.r2s.mobilestore.service.*;
import com.r2s.mobilestore.service.FileService;
import com.r2s.mobilestore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author: NguyenTienDat
 * @function: create, update, filter product
 * @Author: VoTien, huuduc, ngohoangkhactuong, tuongvi, phuocduc
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private FileService fileService;
    @Autowired
    private ColorService colorService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private MemoryService memoryService;
    @Autowired
    private ProductTechService productTechService;
    @Autowired
    private SeriService seriService;
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ImageRepository imageReponsetory;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoriesMapper categoriesMapper;
    @Autowired
    private ManufacturerMapper manufacturerMapper;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private ProductTechMapper productTechMapper;
    @Autowired
    private ReviewMapper reviewMapper;

    /**
     * Method create product
     *
     * @param productDTO
     * @param fileImages
     * @return ProductDTO
     * @author NguyenTienDat
     */
    @Override
    @Transactional
    public ProductDTO create(ProductDTO productDTO, List<MultipartFile> fileImages) {

        Map<String, Object> errors = new HashMap<>();
        Product product = productMapper.toEntity(productDTO);

        //create a variable ImageDTO list to include image filename
        List<ImageDTO> imageDTOs = new ArrayList<>();
        // check if file input is not null then get file in list file
        if (fileImages != null) {

            fileImages.forEach(file -> {

                String fileName = fileService.uploadFile(file);
                // check if file name of any file not be null
                if (fileName == null) {

                    errors.put("Images not be null!", productDTO.getName());
                }

                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setName(fileName);
                imageDTOs.add(imageDTO);
            });

            productDTO.setImageDTOs(imageDTOs);

        } else {
            errors.put("Images not be null!", productDTO.getName());
        }
        //check product name exists in database
        if (productRepository.existsByName(productDTO.getName()))

            errors.put("product name", productDTO.getName());

        checkInputProductDTO(errors, productDTO);

        Categories categories = categoriesMapper.toEntity(productDTO.getCategoriesDTO());
        product.setCategory(categories);

        Manufacturer manufacturer = manufacturerMapper.toEntity(productDTO.getManufacturerDTO());
        product.setManufacturer(manufacturer);

        //save new product into database end get saved product to create other DTO of this product
        Product new_product = productRepository.save(product);
        long id = new_product.getId();
        return createDTOsForProduct(new_product, productDTO);
    }

    /**
     * @param errors
     * @param productDTO
     * @author NguyenTienDat
     */
    public void checkInputProductDTO(Map<String, Object> errors, ProductDTO productDTO) {

        if (productDTO == null)
            errors.put("productDTO not be null!", productDTO.getName());

        if (productDTO.getCategoriesDTO() == null)
            errors.put("Categories not be null!", productDTO.getName());

        if (productDTO.getManufacturerDTO() == null)
            errors.put("manufacturer not be null!", productDTO.getName());

        if (productDTO.getColorDTOs() == null || productDTO.getColorDTOs().size() == 0)
            errors.put("colors not be null!", productDTO.getName());

        if (productDTO.getMemoryDTOs() == null || productDTO.getMemoryDTOs().size() == 0)
            errors.put("memories not be null!", productDTO.getName());

        if (productDTO.getSeriDTOs() == null || productDTO.getSeriDTOs().size() == 0)
            errors.put("series not be null!", productDTO.getName());

        if (productDTO.getProductTechDTOs() == null || productDTO.getProductTechDTOs().size() == 0)
            errors.put("productTechs not be null!", productDTO.getName());
        if (errors.size() > 0) {
            throw new ConflictException(Collections.singletonMap("ProductDTO", errors));
        }
    }

    /**
     * @param product
     * @param productDTO
     * @return ProductDTO
     * @author NguyenTienDat
     */
    public ProductDTO createDTOsForProduct(Product product, ProductDTO productDTO) {
        // create lists DTO of product
        List<ColorDTO> colorDTOs = colorService.createProductColor(product, productDTO.getColorDTOs());
        List<ImageDTO> imageDTOs = imageService.createProductImage(product, productDTO.getImageDTOs());
        List<MemoryDTO> memoryDTOs = memoryService.createProductMemory(product, productDTO.getMemoryDTOs());
        List<SeriDTO> seriDTOs = seriService.createProductSeri(product, productDTO.getSeriDTOs());
        List<ProductTechDTO> productTechDTOs = productTechService.createProductTech(product, productDTO.getProductTechDTOs());

        ProductDTO productDTOResult = productMapper.toDTO(product);
        // set DTOs information for productDTO view
        productDTOResult.setColorDTOs(colorDTOs);
        productDTOResult.setImageDTOs(imageDTOs);
        productDTOResult.setMemoryDTOs(memoryDTOs);
        productDTOResult.setSeriDTOs(seriDTOs);
        productDTOResult.setProductTechDTOs(productTechDTOs);

        return productDTOResult;
    }

    /**
     * @param id
     * @param productDTO
     * @param fileImages
     * @return ProductDTO
     * @author NguyenTienDat
     */

    @Override
    @Transactional
    public ProductDTO update(long id, ProductDTO productDTO, List<MultipartFile> fileImages) {

        Map<String, Object> errors = new HashMap<>();

        //create a variable ImageDTO list to include image filename
        List<ImageDTO> imageDTOs = new ArrayList<>();

        //Check if the product is already in the database before updating
        Product product = productRepository.findById(id).orElseThrow(

                () -> new ResourceNotFoundException(Collections.singletonMap("ProductDTO", id)));

        //Check All file input is null or not null
        // If null then not update
        //If file name is null then add old image into imageDTO
        if (fileImages != null) {

            fileImages.forEach(file -> {
                String fileName = fileService.uploadFile(file);

                //Check file name of file input
                // If file name is null then add old image into imageDTO
                if (fileName == null) {
                    imageReponsetory.findByProductId(product.getId()).forEach(image -> {
                        imageDTOs.add(imageMapper.toDTO(image));
                    });
                }

                //file name is not null then add image into image DTO to update new image
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setName(fileName);
                imageDTOs.add(imageDTO);
            });
        } else {

            imageReponsetory.findByProductId(product.getId()).forEach(image -> {

                imageDTOs.add(imageMapper.toDTO(image));
            });
        }

        productDTO.setImageDTOs(imageDTOs);

        //check input of productDTO
        checkInputProductDTO(errors, productDTO);

        // Check if exists errors then throw object errors with message errors
        if (errors.size() > ENum.ZERO.getValue())

            throw new ConflictException(Collections.singletonMap("ProductDTO", errors));

        // create a variable Product will be updated into database
        Product productUpdated = productMapper.toEntity(productDTO);
        productUpdated.setId(product.getId());

        Categories categories = categoriesMapper.toEntity(productDTO.getCategoriesDTO());
        productUpdated.setCategory(categories);

        Manufacturer manufacturer = manufacturerMapper.toEntity(productDTO.getManufacturerDTO());
        productUpdated.setManufacturer(manufacturer);

        Product productUpdateResult;

        //Check if productDTO name is not equals product existed in database
        if (!product.getName().equals(productDTO.getName())) {

            // Check productDTO name with orther product in database
            if (productRepository.existsByName(productDTO.getName()))

                errors.put("product name existed", productDTO.getName());

            if (errors.size() > ENum.ZERO.getValue())

                throw new ConflictException(Collections.singletonMap("ProductDTO", errors));
            productUpdateResult = productRepository.save(productUpdated);

            return updateDTOsForProduct(productUpdateResult, productDTO);
        }

        if (product.getReviews() != null) {

            productUpdated.setReviews(product.getReviews());

        }

        // ProductDTO name equals Old Product name and unique
        productUpdateResult = productRepository.save(productUpdated);

        return updateDTOsForProduct(productUpdateResult, productDTO);
    }

    /**
     * @param product
     * @param productDTO
     * @return ProductDTO
     * @author NguyenTienDat
     */
    public ProductDTO updateDTOsForProduct(Product product, ProductDTO productDTO) {

        // update lists DTO of product
        List<ColorDTO> colorDTOs = colorService.updateProductColor(product, productDTO.getColorDTOs());
        List<ImageDTO> imageDTOs = imageService.updateProductImage(product, productDTO.getImageDTOs());
        List<MemoryDTO> memoryDTOs = memoryService.updateProductMemory(product, productDTO.getMemoryDTOs());
        List<SeriDTO> seriDTOs = seriService.updateProductSeri(product, productDTO.getSeriDTOs());
        List<ProductTechDTO> productTechDTOs = productTechService.updateProductTech(product, productDTO.getProductTechDTOs());

        ProductDTO productDTOResult = productMapper.toDTO(product);
        List<ReviewDTO> reviewDTOList = new ArrayList<>();

        if (product.getReviews() != null) {

            for (Review review : product.getReviews()) {

                if (review.isStatus()) {

                    reviewDTOList.add(reviewMapper.toDTO(review));
                }
            }
        }
        productDTOResult.setReviewDTOs(reviewDTOList);

        // set lists DTO for productDTO view
        productDTOResult.setColorDTOs(colorDTOs);
        productDTOResult.setImageDTOs(imageDTOs);
        productDTOResult.setMemoryDTOs(memoryDTOs);
        productDTOResult.setSeriDTOs(seriDTOs);
        productDTOResult.setProductTechDTOs(productTechDTOs);


        return productDTOResult;
    }


    /**
     * @param no
     * @param limit
     * @return PaginationDTO
     * @author NguyenTienDat
     */
    @Override
    public PaginationDTO findAllActiveProduct(int no, int limit) {

        Page<ProductDTO> page = productRepository.findAllActive(PageRequest.of(no, limit))
                .map(item -> {

                    ProductDTO productDTO = productMapper.toDTO(item);

                    List<ReviewDTO> reviewDTOS = new ArrayList<>();

                    item.getReviews().forEach(review -> {

                        if (review.isStatus()) {

                            reviewDTOS.add(reviewMapper.toDTO(review));
                        }
                    });

                    productDTO.setReviewDTOs(reviewDTOS);

                    List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                    item.getProductTechs().forEach(productTech -> {

                        productTechDTOs.add(productTechMapper.toDTO(productTech));
                    });

                    productDTO.setProductTechDTOs(productTechDTOs);

                    return productDTO;

                });

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }

    /**
     * @param keyword
     * @param no
     * @param limit
     * @return List<ShowProductDTO>
     * @Author VoTien
     */
    public PaginationDTO searchProduct(String keyword, int no, int limit) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<ShowProductDTO> page;

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Role_Admin"))) {

            page = productRepository.findProductByKeywordAdmin(keyword, PageRequest.of(no, limit)).map(
                    product -> productMapper.toShowProductDTO(product));

        } else {

            page = productRepository.findProductByKeywordCustomer(keyword, PageRequest.of(no, limit)).map(
                    product -> productMapper.toShowProductDTO(product));
        }

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());

    }

    /**
     * @param id
     * @return true
     * @author huuduc
     */
    @Override
    public boolean deleteById(long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        if (!product.isStatus()) {
            throw new ResourceNotFoundException(Collections.singletonMap("id", id));
        }

        //Check if the promotion exists in any orders
        if (ordersRepository.existsByPromotionId(id)) {

            throw new CannotDeleteException(id);
        }

        product.setColors(null);
        product.setProductTechs(null);
        product.setImages(null);
        product.setSeries(null);
        product.setReviews(null);
        product.setStatus(false);
        product.setMemories(null);
        this.productRepository.save(product);

        return true;
    }


    /**
     * Method show new Product
     *
     * @return show 8 new product items ProductDTO
     * @Author Haunv
     */
    @Override
    public List<ProductDTO>  findNewProduct() {

        List<ProductDTO> productDTOList = productRepository.findNewProductWithLimit()
                .stream().map(item -> {
                    ProductDTO productDTO = productMapper.toDTO(item);

                    List<ReviewDTO> reviewDTOS = new ArrayList<>();

                    item.getReviews().forEach(review -> {
                        if (review.isStatus()) {

                            reviewDTOS.add(reviewMapper.toDTO(review));
                        }
                    });

                    productDTO.setReviewDTOs(reviewDTOS);

                    List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                    item.getProductTechs().forEach(productTech -> {
                        productTechDTOs.add(productTechMapper.toDTO(productTech));
                    });

                    productDTO.setProductTechDTOs(productTechDTOs);

                    return productDTO;

                }).collect(Collectors.toList());

        return productDTOList;
    }

    /**
     * Method Show list product by category
     *
     * @param categoryId
     * @param no
     * @param limit
     * @return PaginationDTO (productDTO)
     * @author ngohoangkhactuong
     */
    @Override
    public PaginationDTO showListProductPagination(long categoryId, int no, int limit) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<ProductDTO> page;

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Role_Admin"))) {

            page = productRepository.showListProductPaginationAdmin(categoryId,
                            PageRequest.of(no, limit))
                    .map(item -> {

                        ProductDTO productDTO = productMapper.toDTO(item);

                        List<ReviewDTO> reviewDTOS = new ArrayList<>();

                        item.getReviews().forEach(review -> {

                            if (review.isStatus()) {

                                reviewDTOS.add(reviewMapper.toDTO(review));
                            }
                        });

                        productDTO.setReviewDTOs(reviewDTOS);

                        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                        item.getProductTechs().forEach(productTech -> {

                            productTechDTOs.add(productTechMapper.toDTO(productTech));
                        });

                        productDTO.setProductTechDTOs(productTechDTOs);

                        return productDTO;

                    });
        } else {
            page = productRepository.showListProductPaginationCustomer(categoryId,
                            PageRequest.of(no, limit))
                    .map(item -> {

                        ProductDTO productDTO = productMapper.toDTO(item);

                        List<ReviewDTO> reviewDTOS = new ArrayList<>();

                        item.getReviews().forEach(review -> {

                            if (review.isStatus()) {

                                reviewDTOS.add(reviewMapper.toDTO(review));
                            }
                        });

                        productDTO.setReviewDTOs(reviewDTOS);

                        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                        item.getProductTechs().forEach(productTech -> {
                            productTechDTOs.add(productTechMapper.toDTO(productTech));
                        });

                        productDTO.setProductTechDTOs(productTechDTOs);

                        return productDTO;

                    });
        }

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }

    @Override
    public PaginationDTO showProductByCategoryAndManufacturer(long category_id, long manufacturer_id,
                                                              int no, int limit) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<ProductDTO> page;

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Role_Admin"))) {

            page = productRepository.showListProductByCategoryAndManufacturerAdmin(category_id,
                            manufacturer_id, PageRequest.of(no, limit))
                    .map(item -> {

                        ProductDTO productDTO = productMapper.toDTO(item);

                        List<ReviewDTO> reviewDTOS = new ArrayList<>();

                        item.getReviews().forEach(review -> {

                            if (review.isStatus()) {

                                reviewDTOS.add(reviewMapper.toDTO(review));
                            }
                        });
                        productDTO.setReviewDTOs(reviewDTOS);

                        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                        item.getProductTechs().forEach(productTech -> {
                            productTechDTOs.add(productTechMapper.toDTO(productTech));
                        });

                        productDTO.setProductTechDTOs(productTechDTOs);

                        return productDTO;
                    });


        } else {
            page = productRepository.showListProductByCategoryAndManufacturerCustomer(category_id,
                            manufacturer_id, PageRequest.of(no, limit))
                    .map(item -> {
                        ProductDTO productDTO = productMapper.toDTO(item);

                        List<ReviewDTO> reviewDTOS = new ArrayList<>();

                        item.getReviews().forEach(review -> {

                            if (review.isStatus()) {

                                reviewDTOS.add(reviewMapper.toDTO(review));
                            }
                        });
                        productDTO.setReviewDTOs(reviewDTOS);

                        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                        item.getProductTechs().forEach(productTech -> {
                            productTechDTOs.add(productTechMapper.toDTO(productTech));
                        });

                        productDTO.setProductTechDTOs(productTechDTOs);

                        return productDTO;

                    });
        }

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }

    /**
     * Method related products by category and manufacturer (default = 6)
     *
     *  @param productId
     *  @param quantity
     *  @return List<ShowProductRelated>
     *  @Author phuocduc
     *
     * */

    @Override
    public List<ShowProductRelated> showRelatedProduct(long productId, int quantity) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ConflictException(Collections.singletonMap("id", productId))
        );

        long categoryId = product.getCategory().getId();
        long manufacturerId = product.getManufacturer().getId();

        Page<Product> products = productRepository.showListProductByCategoryAndManufacturerAdmin(
                categoryId, manufacturerId, PageRequest.of(0, quantity));

        List<ShowProductRelated> productsRelated = new ArrayList<>();

        for (Product prod : products.getContent()) {

            if (!prod.getName().equals(product.getName())) {

                productsRelated.add(setProductToShowProductRelatedDTO(prod)); //convert to dto
            }

        }


        return productsRelated;
    }


    public ShowProductRelated setProductToShowProductRelatedDTO(Product product) {

        ShowProductRelated showProductDTO = new ShowProductRelated();
        showProductDTO.setId(product.getId());
        showProductDTO.setName(product.getName());
        showProductDTO.setPrice(product.getPrice());
        showProductDTO.setDescription(product.getDescription());

        List<ImageDTO> list = new ArrayList<>();
        product.getImages().forEach(
                item -> {
                    ImageDTO imageDTO = imageMapper.toDTO(item);
                    list.add(imageDTO);
                }
        );
        showProductDTO.setImageDTOs(list);

        return showProductDTO;

    }

    /**
     * Method show detail product and increase view
     *
     * @param id
     * @return productDTO
     * @author tuongvi
     */
    public ProductDTO showDetailProduct(long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Collections.singletonMap("product Id", id)));

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Role_Customer")) &
                !product.isStatus()) {

            throw new ResourceNotFoundException(Collections.singletonMap("id", product.getId()));
        }

        int views = product.getViews();
        final int plus = 1;
        product.setViews(views + plus);

        Product productSaved = productRepository.save(product);
        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

        productSaved.getProductTechs().forEach(productTech -> {

            productTechDTOs.add(productTechMapper.toDTO(productTech));
        });

        ProductDTO productDTO = this.productMapper.toDTO(product);

        productDTO.setProductTechDTOs(productTechDTOs);
        List<ReviewDTO> reviewDTOS = new ArrayList<>();

        productSaved.getReviews().forEach(review -> {

            if (review.isStatus()) {

                reviewDTOS.add(reviewMapper.toDTO(review));

            }
        });

        int sum = 0;
        for (Review review : productSaved.getReviews()) {
            sum += review.getRating();
        }

        float star = 0;
        if ((productSaved.getReviews().size()) != 0) {
            star = ((float) sum ) / (productSaved.getReviews().size());

        }
        productDTO.setReviewDTOs(reviewDTOS);
        productDTO.setStar((float) Math.round(star * 10) / 10);

        return productDTO;
    }

    /**
     * method search products by many params
     *
     * @param filterProductDTO
     * @param no
     * @param limit
     * @return PaginationDTO
     * @method show list product for function search
     * @author NguyenTienDat
     */
    @Override
    public PaginationDTO showListProductFilter(FilterProductDTO filterProductDTO, int no, int limit) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<ProductDTO> page;


        int manufacturerId = filterProductDTO.getManufactureId();
        int categoryId = filterProductDTO.getCategoryId();
        String keyword = filterProductDTO.getKeyword();
        BigDecimal lowerPrice = filterProductDTO.getLowerPrice();
        BigDecimal higherPrice = filterProductDTO.getHigherPrice();

        if (higherPrice == null) {
            higherPrice = BigDecimal.valueOf(10000000);
        }


        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Role_Admin"))) {

            page = productRepository
                    .findProductsByFullFilterAdmin(manufacturerId, categoryId, keyword, lowerPrice, higherPrice, PageRequest.of(no, limit))

                    .map(item -> {
                        ProductDTO productDTO = productMapper.toDTO(item);

                        List<ReviewDTO> reviewDTOS = new ArrayList<>();
                        item.getReviews().forEach(review -> {

                            if (review.isStatus()) {

                                reviewDTOS.add(reviewMapper.toDTO(review));
                            }
                        });

                        productDTO.setReviewDTOs(reviewDTOS);

                        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                        item.getProductTechs().forEach(productTech -> {
                            productTechDTOs.add(productTechMapper.toDTO(productTech));
                        });

                        productDTO.setProductTechDTOs(productTechDTOs);

                        return productDTO;
                    });


        } else {
            page = productRepository

                    .findProductsByFullFilterCustomer(manufacturerId, categoryId, keyword, lowerPrice, higherPrice,
                            PageRequest.of(no, limit))

                    .map(item -> {
                        ProductDTO productDTO = productMapper.toDTO(item);

                        List<ReviewDTO> reviewDTOS = new ArrayList<>();

                        item.getReviews().forEach(review -> {

                            if (review.isStatus()) {

                                reviewDTOS.add(reviewMapper.toDTO(review));
                            }
                        });

                        productDTO.setReviewDTOs(reviewDTOS);

                        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                        item.getProductTechs().forEach(productTech -> {

                            productTechDTOs.add(productTechMapper.toDTO(productTech));
                        });

                        productDTO.setProductTechDTOs(productTechDTOs);

                        return productDTO;

                    });
        }

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }


    public ProductDTO setProductTechDTOforProductDTO(Product product) {

        ProductDTO productDTO = productMapper.toDTO(product);
        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

        product.getProductTechs().forEach(productTech -> {

            ProductTechDTO productTechDTO = productTechMapper.toDTO(productTech);
            productTechDTOs.add(productTechDTO);
        });

        productDTO.setProductTechDTOs(productTechDTOs);

        return productDTO;

    }


    /**
     * Method show active product by category
     *
     * @param id, int no, int limit
     * @return PaginationDTO (productDTO)
     * @author phuocduc
     */
    @Override
    public PaginationDTO showActiveProductByCategory(long id, int no, int limit) {

        Categories categories = categoriesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id))
        );

        Page<ProductDTO> page = productRepository
                .findActiveProductByCategory(categories.getId(), PageRequest.of(no, limit))
                .map(item -> {

                    ProductDTO productDTO = productMapper.toDTO(item);

                    List<ReviewDTO> reviewDTOS = new ArrayList<>();

                    item.getReviews().forEach(review -> {

                        if (review.isStatus()) {

                            reviewDTOS.add(reviewMapper.toDTO(review));
                        }
                    });

                    productDTO.setReviewDTOs(reviewDTOS);

                    List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                    item.getProductTechs().forEach(productTech -> {

                        productTechDTOs.add(productTechMapper.toDTO(productTech));
                    });

                    productDTO.setProductTechDTOs(productTechDTOs);

                    return productDTO;

                });

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }

    /*
     *  Method show active product by manufacturer
     *
     *  @param manufacturerId
     *  @param no
     *  @param limit
     *  @return PaginationDTO (productDTO)
     *  @author phuocduc
     *
     * */
    @Override
    public PaginationDTO showActiveProductByManufacturer(long id, int no, int limit) {

        Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id))
        );

        Page<ProductDTO> page = productRepository
                .findActiveProductByManufacturer(manufacturer.getId(), PageRequest.of(no, limit))
                .map(item -> {

                    ProductDTO productDTO = productMapper.toDTO(item);

                    List<ReviewDTO> reviewDTOS = new ArrayList<>();

                    item.getReviews().forEach(review -> {

                        if (review.isStatus()) {

                            reviewDTOS.add(reviewMapper.toDTO(review));
                        }
                    });

                    productDTO.setReviewDTOs(reviewDTOS);

                    List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                    item.getProductTechs().forEach(productTech -> {

                        productTechDTOs.add(productTechMapper.toDTO(productTech));
                    });

                    productDTO.setProductTechDTOs(productTechDTOs);

                    return productDTO;

                });

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());

    }


    /**
     * Method filter product by price
     *
     * @param lowerPrice
     * @param higherPrice
     * @param no
     * @param limit
     * @return PaginationDTO (productDTO)
     * @author tuongvi
     */
    @Override
    public PaginationDTO FilterProductsByPrice(BigDecimal lowerPrice, BigDecimal higherPrice, int no,
                                               int limit) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<ProductDTO> page;

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Role_Admin"))) {

            page = productRepository.FilterProductsByPriceAdmin(
                            lowerPrice, higherPrice, PageRequest.of(no, limit))
                    .map(item -> {

                        ProductDTO productDTO = productMapper.toDTO(item);

                        List<ReviewDTO> reviewDTOS = new ArrayList<>();

                        item.getReviews().forEach(review -> {

                            if (review.isStatus()) {

                                reviewDTOS.add(reviewMapper.toDTO(review));
                            }
                        });

                        productDTO.setReviewDTOs(reviewDTOS);

                        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                        item.getProductTechs().forEach(productTech -> {

                            productTechDTOs.add(productTechMapper.toDTO(productTech));
                        });

                        productDTO.setProductTechDTOs(productTechDTOs);

                        return productDTO;

                    });
        } else {
            page = productRepository.FilterProductsByPriceCustomer(
                            lowerPrice, higherPrice, PageRequest.of(no, limit))
                    .map(item -> {

                        ProductDTO productDTO = productMapper.toDTO(item);

                        List<ReviewDTO> reviewDTOS = new ArrayList<>();

                        item.getReviews().forEach(review -> {

                            if (review.isStatus()) {

                                reviewDTOS.add(reviewMapper.toDTO(review));
                            }
                        });

                        productDTO.setReviewDTOs(reviewDTOS);

                        List<ProductTechDTO> productTechDTOs = new ArrayList<>();

                        item.getProductTechs().forEach(productTech -> {

                            productTechDTOs.add(productTechMapper.toDTO(productTech));
                        });

                        productDTO.setProductTechDTOs(productTechDTOs);

                        return productDTO;

                    });
        }


        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }
}

