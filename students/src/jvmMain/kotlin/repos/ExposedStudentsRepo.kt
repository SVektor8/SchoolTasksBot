package center.sciprog.tasks_bot.students.repos

import center.sciprog.tasks_bot.students.models.NewStudent
import center.sciprog.tasks_bot.students.models.RegisteredStudent
import center.sciprog.tasks_bot.students.models.StudentId
import dev.inmo.micro_utils.repos.exposed.AbstractExposedCRUDRepo
import dev.inmo.micro_utils.repos.exposed.initTable
import dev.inmo.tgbotapi.types.UserId
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ISqlExpressionBuilder
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedStudentsRepo(
    override val database: Database
) : StudentsRepo, AbstractExposedCRUDRepo<RegisteredStudent, StudentId, NewStudent>(
    tableName = "students"
) {
    val idColumn = long("id").autoIncrement()
    private val userIdColumn = long("userId").uniqueIndex()

    override val primaryKey: PrimaryKey = PrimaryKey(idColumn)

    override val selectById: ISqlExpressionBuilder.(StudentId) -> Op<Boolean> = {
        idColumn.eq(it.long)
    }
    override val ResultRow.asId: StudentId
        get() = StudentId(get(idColumn))
    override val ResultRow.asObject: RegisteredStudent
        get() = RegisteredStudent(
            asId,
            UserId(get(userIdColumn))
        )

    init {
        initTable()
    }

    override fun update(id: StudentId?, value: NewStudent, it: UpdateBuilder<Int>) {
        it[userIdColumn] = value.userId.chatId
    }

    override fun InsertStatement<Number>.asObject(value: NewStudent): RegisteredStudent = RegisteredStudent(
        StudentId(get(idColumn)),
        UserId(get(userIdColumn))
    )

    override suspend fun getById(userId: UserId): RegisteredStudent? = transaction(database) {
        select { userIdColumn.eq(userId.chatId) }.limit(1).firstOrNull() ?.asObject
    }
}
