import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.vcsUtil.VcsUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitCommandResult
import git4idea.commands.GitLineHandler

@Service(Service.Level.PROJECT)
class MyProjectService(private val project: Project) {
    data class CommitDetails(
        val hash: String,
        val author: String,
        val date: String,
        val lineNumber: Int,
        val message: String,
        val content: String
    )

    private fun runCommand(lineHandler: GitLineHandler): GitCommandResult {
        return Git.getInstance().runCommand(lineHandler)
    }

    fun getGitBlame(startLine: Int? = null, endLine: Int? = null): List<CommitDetails>? {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return null
        val document = editor.document
        val file = FileDocumentManager.getInstance().getFile(document) ?: return null
        val directory = file.parent ?: return null
        val filePath = VcsUtil.getFilePath(file)

        val blameParameters = mutableListOf<String>()
        if (startLine != null && endLine != null) {
            val adjustedStartLine = startLine + 1
            val adjustedEndLine = endLine + 1
            blameParameters.add("-L$adjustedStartLine,$adjustedEndLine")
        }
        blameParameters.add(filePath.path)

        val blameRun = GitLineHandler(project, directory, GitCommand.BLAME).apply {
            addParameters(*blameParameters.toTypedArray())
        }

        val blameCommandResponse = runCommand(blameRun)

        val regex = """(\w+) \((.+?)\s+([\d-]+ [\d:]+ [+-]\d+)\s+(\d+)\)\s*(.*)""".toRegex()
        val commitsDetailsList = mutableListOf<CommitDetails>()

        blameCommandResponse.output.forEach { line ->
            regex.find(line)?.let { matchResult ->
                val hash = matchResult.groups[1]?.value.orEmpty()
                val author = matchResult.groups[2]?.value.orEmpty()
                val date = matchResult.groups[3]?.value.orEmpty()
                val lineNumber = matchResult.groups[4]?.value?.toIntOrNull() ?: 0
                val content = matchResult.groups[5]?.value.orEmpty()

                val logRun = GitLineHandler(project, directory, GitCommand.LOG).apply {
                    addParameters("-1", "--pretty=format:%s", hash)
                }
                val logCommandResponse = runCommand(logRun)
                val message = logCommandResponse.output.firstOrNull().orEmpty()

                commitsDetailsList.add(
                    CommitDetails(hash, author, date, lineNumber, message, content)
                )
            }
        }

        return commitsDetailsList.distinctBy { it.hash }
    }

}
