<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getBooks, deleteBook } from "@/api/book";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();

// 搜索条件
const searchForm = reactive({
  title: "",
  author: "",
  category: "",
  status: "",
});

// 表格数据
const bookList = ref([]);
const total = ref(0);
const loading = ref(false);
const pagination = reactive({
  page: 0,
  size: 10,
  sort: "title,asc",
});

// 图书分类选项
const categoryOptions = ref([
  { label: "All Categories", value: "" },
  { label: "Computer Science", value: "Computer Science" },
  { label: "Literature", value: "Literature" },
  { label: "History", value: "History" },
  { label: "Art", value: "Art" },
  { label: "Science", value: "Science" },
  { label: "Economics", value: "Economics" },
]);

// 图书状态选项
const statusOptions = ref([
  { label: "All Status", value: "" },
  { label: "Available", value: "AVAILABLE" },
  { label: "Unavailable", value: "UNAVAILABLE" },
]);

// 获取图书列表
const fetchBooks = async () => {
  loading.value = true;
  try {
    const res = await getBooks({
      ...searchForm,
      page: pagination.page,
      size: pagination.size,
      sort: pagination.sort,
    });
    if (res.code === 200) {
      bookList.value = res.data.content;
      total.value = res.data.totalElements;
    }
  } catch (error) {
    console.error("Failed to fetch books:", error);
    ElMessage.error("Failed to get book list");
  } finally {
    loading.value = false;
  }
};

// 处理表格排序变化
const handleSortChange = ({ prop, order }) => {
  if (prop && order) {
    pagination.sort = `${prop},${order === "ascending" ? "asc" : "desc"}`;
  } else {
    pagination.sort = "title,asc";
  }
  fetchBooks();
};

// 处理页码变化
const handleCurrentChange = (val) => {
  pagination.page = val - 1;
  fetchBooks();
};

// 处理每页条数变化
const handleSizeChange = (val) => {
  pagination.size = val;
  pagination.page = 0;
  fetchBooks();
};

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach((key) => {
    searchForm[key] = "";
  });
  pagination.page = 0;
  fetchBooks();
};

// 添加图书
const handleAddBook = () => {
  router.push("/admin/book-edit");
};

// 编辑图书
const handleEditBook = (row) => {
  router.push(`/admin/book-edit/${row.bookId}`);
};

// 删除图书
const handleDeleteBook = async (row) => {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to delete "${row.title}"? This action cannot be undone!`,
      "Delete Confirmation",
      {
        confirmButtonText: "Delete",
        cancelButtonText: "Cancel",
        type: "warning",
      }
    );

    const res = await deleteBook(row.bookId);
    if (res.code === 200) {
      ElMessage.success("Book deleted successfully");
      fetchBooks(); // refresh list
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "Failed to delete");
    }
  }
};

// 格式化发布日期
const formatPublishDate = (date) => {
  if (!date) return "-";
  return new Date(date).toLocaleDateString();
};

// 首次加载
onMounted(() => {
  fetchBooks();
});
</script>

<template>
  <div class="book-manage-container">
    <!-- Search and Actions Bar -->
    <el-card class="search-card">
      <div class="top-actions">
        <el-button type="primary" @click="handleAddBook">
          <el-icon><Plus /></el-icon>Add Book
        </el-button>
      </div>

      <el-form :model="searchForm" inline>
        <el-form-item label="Title">
          <el-input
            v-model="searchForm.title"
            placeholder="Enter book title"
            clearable
            @keyup.enter="fetchBooks"
          />
        </el-form-item>

        <el-form-item label="Author">
          <el-input
            v-model="searchForm.author"
            placeholder="Enter author name"
            clearable
            @keyup.enter="fetchBooks"
          />
        </el-form-item>

        <el-form-item label="Category">
          <el-input
            v-model="searchForm.category"
            placeholder="Enter category"
            clearable
            @keyup.enter="fetchBooks"
          />
        </el-form-item>

        <el-form-item label="Status">
          <el-select
            v-model="searchForm.status"
            placeholder="Select status"
            style="width: 100px"
            clearable
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchBooks">
            <el-icon><Search /></el-icon>Search
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><RefreshLeft /></el-icon>Reset
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图书列表 -->
    <el-card class="book-table-card">
      <el-table
        :data="bookList"
        border
        v-loading="loading"
        @sort-change="handleSortChange"
        style="width: 100%"
      >
        <el-table-column
          prop="title"
          label="Title"
          sortable="custom"
          min-width="200"
        />

        <el-table-column
          prop="author"
          label="Author"
          sortable="custom"
          min-width="120"
        />

        <el-table-column prop="isbn" label="ISBN" min-width="150" />

        <el-table-column prop="publisher" label="Publisher" min-width="150" />

        <el-table-column prop="publishDate" label="Publish Date" min-width="120">
          <template #default="{ row }">
            {{ formatPublishDate(row.publishDate) }}
          </template>
        </el-table-column>

        <el-table-column
          prop="category"
          label="Category"
          sortable="custom"
          min-width="100"
        />

        <el-table-column
          prop="stock"
          label="Stock"
          width="80"
          sortable="custom"
        />

        <el-table-column prop="status" label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'AVAILABLE' ? 'success' : 'danger'">
              {{ row.status === "AVAILABLE" ? "Available" : "Unavailable" }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Actions" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEditBook(row)">
              Edit
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteBook(row)"
            >
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          :current-page="pagination.page + 1"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.book-manage-container {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.top-actions {
  margin-bottom: 20px;
}

.book-table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
