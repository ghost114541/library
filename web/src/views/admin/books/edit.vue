<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getBookDetail, addBook, updateBook } from "@/api/book";
import { ElMessage } from "element-plus";

const route = useRoute();
const router = useRouter();

const bookId = route.params.id;
const isEdit = !!bookId;
const title = isEdit ? "Edit Book" : "Add Book";

// 图书表单
const bookForm = reactive({
  title: "",
  author: "",
  isbn: "",
  publisher: "",
  publishDate: "",
  stock: 1,
  category: "",
  tags: "",
  description: "",
  status: "AVAILABLE",
});

// 表单验证规则
const rules = {
  title: [{ required: true, message: "Please enter the book title", trigger: "blur" }],
  author: [{ required: true, message: "Please enter the author", trigger: "blur" }],
  isbn: [
    { required: true, message: "Please enter ISBN", trigger: "blur" },
    { pattern: /^[0-9-]{10,17}$/, message: "Invalid ISBN format", trigger: "blur" },
  ],
  publisher: [{ required: true, message: "Please enter the publisher", trigger: "blur" }],
  publishDate: [{ required: true, message: "Please select publish date", trigger: "blur" }],
  stock: [
    { required: true, message: "Please enter stock quantity", trigger: "blur" },
    { type: "number", min: 0, message: "Stock cannot be less than 0", trigger: "blur" },
  ],
  category: [{ required: true, message: "Please select a category", trigger: "change" }],
};

// 图书分类选项
const categoryOptions = ref([
  { label: "Computer Science", value: "Computer Science" },
  { label: "Literature", value: "Literature" },
  { label: "History", value: "History" },
  { label: "Art", value: "Art" },
  { label: "Science", value: "Science" },
  { label: "Economics", value: "Economics" },
]);

// 图书状态选项
const statusOptions = ref([
  { label: "Available", value: "AVAILABLE" },
  { label: "Unavailable", value: "UNAVAILABLE" },
]);

const bookFormRef = ref(null);
const loading = ref(false);

// 如果是编辑模式，获取图书详情
const fetchBookDetail = async () => {
  if (!isEdit) return;

  loading.value = true;
  try {
    const res = await getBookDetail(bookId);
    if (res.code === 200) {
      // 将返回的数据填充到表单中
      Object.keys(bookForm).forEach((key) => {
        if (res.data[key] !== undefined) {
          bookForm[key] = res.data[key];
        }
      });
    }
  } catch (error) {
    ElMessage.error("Failed to get book details");
    console.error("Failed to fetch book details:", error);
  } finally {
    loading.value = false;
  }
};

// 提交表单
const submitForm = () => {
  bookFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        let res;
        if (isEdit) {
          // 更新图书
          res = await updateBook(bookId, bookForm);
        } else {
          // 添加图书
          res = await addBook(bookForm);
        }

        if (res.code === 200) {
          ElMessage.success(`Book ${isEdit ? "updated" : "added"} successfully`);
          router.push("/admin/book-manage");
        }
      } catch (error) {
        ElMessage.error(error.message || `Failed to ${isEdit ? "update" : "add"} book`);
      } finally {
        loading.value = false;
      }
    }
  });
};

// 重置表单
const resetForm = () => {
  if (isEdit) {
    // 编辑模式下重置为原始数据
    fetchBookDetail();
  } else {
    // 新增模式下清空表单
    bookFormRef.value.resetFields();
  }
};

// 返回图书管理页
const goBack = () => {
  router.push("/admin/book-manage");
};

onMounted(() => {
  fetchBookDetail();
});
</script>

<template>
  <div class="book-edit-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>{{ title }}</h2>
          <el-button @click="goBack" type="primary" text>
            <el-icon><Back /></el-icon> Back to List
          </el-button>
        </div>
      </template>

      <el-form
        ref="bookFormRef"
        :model="bookForm"
        :rules="rules"
        label-width="100px"
        v-loading="loading"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Title" prop="title">
              <el-input v-model="bookForm.title" placeholder="Enter book title" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Author" prop="author">
              <el-input v-model="bookForm.author" placeholder="Enter author name" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ISBN" prop="isbn">
              <el-input v-model="bookForm.isbn" placeholder="Enter ISBN" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Publisher" prop="publisher">
              <el-input
                v-model="bookForm.publisher"
                placeholder="Enter publisher"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Publish Date" prop="publishDate">
              <el-date-picker
                v-model="bookForm.publishDate"
                type="date"
                placeholder="Select publish date"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Stock" prop="stock">
              <el-input-number
                v-model="bookForm.stock"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Category" prop="category">
              <el-input
                v-model="bookForm.category"
                placeholder="Category"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Status" prop="status">
              <el-select
                v-model="bookForm.status"
                placeholder="Select status"
                style="width: 100%"
              >
                <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="Tags" prop="tags">
          <el-input
            v-model="bookForm.tags"
            placeholder="Enter tags, separated by commas"
          />
        </el-form-item>

        <el-form-item label="Book Description" prop="description">
          <el-input
            v-model="bookForm.description"
            type="textarea"
            rows="4"
            placeholder="Enter book description"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">
            {{ isEdit ? "Update" : "Add" }}
          </el-button>
          <el-button @click="resetForm">Reset</el-button>
          <el-button @click="goBack">Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.book-edit-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
}
</style>
