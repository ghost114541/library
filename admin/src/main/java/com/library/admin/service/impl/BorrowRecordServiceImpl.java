package com.library.admin.service.impl;

import com.library.admin.dto.BorrowRecordDto;
import com.library.admin.entity.Book;
import com.library.admin.entity.BorrowRecord;
import com.library.admin.entity.User;
import com.library.admin.exception.ResourceNotFoundException;
import com.library.admin.repository.BookRepository;
import com.library.admin.repository.BorrowRecordRepository;
import com.library.admin.repository.UserRepository;
import com.library.admin.security.UserDetailsImpl;
import com.library.admin.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 借阅记录服务实现类
 * 提供书籍借阅/归还操作及借阅记录查询功能
 */
@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository; // 借阅记录数据访问对象

    @Autowired
    private UserRepository userRepository; // 用户数据访问对象

    @Autowired
    private BookRepository bookRepository; // 书籍数据访问对象

    /**
     * 用户借阅书籍
     *
     * @param authentication 认证信息（包含当前用户）
     * @param borrowRequest 借阅请求数据
     * @return 创建的借阅记录
     * @throws ResourceNotFoundException 如果用户或书籍不存在
     * @throws IllegalStateException 如果书籍不可用或库存不足
     */
    @Override
    @Transactional
    public BorrowRecordDto borrowBook(Authentication authentication, BorrowRecordDto.BorrowRequest borrowRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));

        Book book = bookRepository.findById(borrowRequest.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", borrowRequest.getBookId()));

        // 检查书籍是否可借阅
        if (book.getStatus() == Book.BookStatus.UNAVAILABLE) {
            throw new IllegalStateException("Book is currently unavailable for borrowing");
        }

        // 检查库存是否充足
        if (book.getStock() <= 0) {
            throw new IllegalStateException("No books available in stock");
        }

        // 设置归还日期（默认14天后）
        LocalDateTime dueDate = borrowRequest.getDueDate();
        if (dueDate == null) {
            dueDate = LocalDateTime.now().plusDays(14);
        }

        // 创建借阅记录
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(LocalDateTime.now());
        borrowRecord.setDueDate(dueDate);
        borrowRecord.setStatus(BorrowRecord.BorrowStatus.BORROWED);

        // 减少书籍库存并更新状态
        book.setStock(book.getStock() - 1);
        if (book.getStock() == 0) {
            book.setStatus(Book.BookStatus.UNAVAILABLE);
        }
        bookRepository.save(book);

        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        return mapToDto(savedRecord);
    }

    /**
     * 用户归还书籍
     *
     * @param authentication 认证信息（包含当前用户）
     * @param returnRequest 归还请求数据
     * @return 更新后的借阅记录
     * @throws ResourceNotFoundException 如果借阅记录不存在
     * @throws IllegalStateException 如果用户无权限或书籍已归还
     */
    @Override
    @Transactional
    public BorrowRecordDto returnBook(Authentication authentication, BorrowRecordDto.ReturnRequest returnRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));

        BorrowRecord borrowRecord = borrowRecordRepository.findById(returnRequest.getRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrow Record", "id", returnRequest.getRecordId()));

        // 权限验证：仅当前用户或管理员可归还
        if (!borrowRecord.getUser().getUserId().equals(user.getUserId()) &&
                user.getRole() != User.UserRole.ROLE_ADMIN) {
            throw new IllegalStateException("You are not authorized to return this book");
        }

        // 检查是否已归还
        if (borrowRecord.getStatus() == BorrowRecord.BorrowStatus.RETURNED) {
            throw new IllegalStateException("This book has already been returned");
        }

        // 更新借阅记录
        borrowRecord.setReturnDate(LocalDateTime.now());
        borrowRecord.setStatus(BorrowRecord.BorrowStatus.RETURNED);

        // 增加书籍库存并更新状态
        Book book = borrowRecord.getBook();
        book.setStock(book.getStock() + 1);
        book.setStatus(Book.BookStatus.AVAILABLE);
        bookRepository.save(book);

        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);
        return mapToDto(updatedRecord);
    }

    /**
     * 查询当前用户的借阅记录
     *
     * @param authentication 认证信息（包含当前用户）
     * @param searchCriteria 搜索条件（用户/书籍/状态等）
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Override
    public Page<BorrowRecordDto> getUserBorrowRecords(Authentication authentication,
                                                      BorrowRecordDto.SearchCriteria searchCriteria, Pageable pageable) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));

        // 构建用户专属记录查询条件
        Specification<BorrowRecord> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 固定过滤当前用户
            predicates.add(criteriaBuilder.equal(root.get("user"), user));

            // 添加其他搜索条件
            addSearchCriteria(searchCriteria, root, criteriaBuilder, predicates);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return borrowRecordRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    /**
     * 查询所有借阅记录（管理员专用）
     *
     * @param searchCriteria 搜索条件（用户/书籍/状态等）
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Override
    public Page<BorrowRecordDto> getAllBorrowRecords(BorrowRecordDto.SearchCriteria searchCriteria, Pageable pageable) {
        // 构建带搜索条件的查询
        Specification<BorrowRecord> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 添加搜索条件
            addSearchCriteria(searchCriteria, root, criteriaBuilder, predicates);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return borrowRecordRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    /**
     * 添加搜索条件到查询谓词
     *
     * @param searchCriteria 搜索条件对象
     * @param root 查询根对象
     * @param criteriaBuilder JPA条件构建器
     * @param predicates 谓词集合
     */
    private void addSearchCriteria(BorrowRecordDto.SearchCriteria searchCriteria,
                                   javax.persistence.criteria.Root<BorrowRecord> root,
                                   javax.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                   List<Predicate> predicates) {
        if (searchCriteria != null) {
            if (searchCriteria.getUserId() != null) {
                User user = userRepository.findById(searchCriteria.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", searchCriteria.getUserId()));
                predicates.add(criteriaBuilder.equal(root.get("user"), user));
            }

            if (searchCriteria.getBookId() != null) {
                Book book = bookRepository.findById(searchCriteria.getBookId())
                        .orElseThrow(() -> new ResourceNotFoundException("Book", "id", searchCriteria.getBookId()));
                predicates.add(criteriaBuilder.equal(root.get("book"), book));
            }

            if (searchCriteria.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), searchCriteria.getStatus()));
            }

            if (Boolean.TRUE.equals(searchCriteria.getOverdue())) {
                predicates.add(criteriaBuilder.lessThan(root.get("dueDate"), LocalDateTime.now()));
                predicates.add(criteriaBuilder.equal(root.get("status"), BorrowRecord.BorrowStatus.BORROWED));
            }
        }
    }

    /**
     * 将借阅记录实体转换为数据传输对象（DTO）
     *
     * @param borrowRecord 借阅记录实体
     * @return 对应的DTO对象
     */
    private BorrowRecordDto mapToDto(BorrowRecord borrowRecord) {
        BorrowRecordDto dto = new BorrowRecordDto();
        dto.setRecordId(borrowRecord.getRecordId());
        dto.setUserId(borrowRecord.getUser().getUserId());
        dto.setUsername(borrowRecord.getUser().getUsername());
        dto.setBookId(borrowRecord.getBook().getBookId());
        dto.setBookTitle(borrowRecord.getBook().getTitle());
        dto.setBorrowDate(borrowRecord.getBorrowDate());
        dto.setDueDate(borrowRecord.getDueDate());
        dto.setReturnDate(borrowRecord.getReturnDate());
        dto.setStatus(borrowRecord.getStatus());

        return dto;
    }
}