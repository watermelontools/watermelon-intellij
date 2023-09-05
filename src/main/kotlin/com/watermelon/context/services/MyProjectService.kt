import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.vcsUtil.VcsUtil
import com.intellij.openapi.fileEditor.FileEditorManager
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

    fun getGitBlame(): List<CommitDetails>? {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return null
        val document = editor.document
        val file = FileDocumentManager.getInstance().getFile(document) ?: return null
        val directory = file.parent ?: return null
        val filePath = VcsUtil.getFilePath(file)
        println("File path : $filePath")
        val blameRun = GitLineHandler(project, directory, GitCommand.BLAME).apply {
            addParameters(filePath.path)
        }

        val blameCommandResponse = runCommand(blameRun)

        // Use regex to extract details from git blame
        val regex = """(\w+) \((.+?)\s+([\d-]+ [\d:]+ [+-]\d+)\s+(\d+)\)\s*(.*)""".toRegex()

        val commitsDetailsList = mutableListOf<CommitDetails>()

        // Extract details and map to a list of CommitDetails
        blameCommandResponse.output.forEach { line ->
            regex.find(line)?.let { matchResult ->
                val hash = matchResult.groups[1]?.value.orEmpty()
                val author = matchResult.groups[2]?.value.orEmpty()
                val date = matchResult.groups[3]?.value.orEmpty()
                val lineNumber = matchResult.groups[4]?.value?.toIntOrNull() ?: 0
                val content = matchResult.groups[5]?.value.orEmpty()

                // Fetch commit message for unique hashes
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

    fun getPartialGitBlame(startLine: Int, endLine: Int): List<CommitDetails>? {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return null
        val document = editor.document
        val file = FileDocumentManager.getInstance().getFile(document) ?: return null
        val directory = file.parent ?: return null
        val filePath = VcsUtil.getFilePath(file)
        // Adjust the line numbers to be 1-indexed
        val adjustedStartLine = startLine + 1
        val adjustedEndLine = endLine + 1

        val blameRun = GitLineHandler(project, directory, GitCommand.BLAME).apply {
            addParameters("-L$adjustedStartLine,$adjustedEndLine", filePath.path)
        }

        val blameCommandResponse = runCommand(blameRun)


        // Use regex to extract details from git blame
        val regex = """(\w+) \((.+?)\s+([\d-]+ [\d:]+ [+-]\d+)\s+(\d+)\)\s*(.*)""".toRegex()

        val commitsDetailsList = mutableListOf<CommitDetails>()

        // Extract details and map to a list of CommitDetails
        blameCommandResponse.output.forEach { line ->
            regex.find(line)?.let { matchResult ->
                val hash = matchResult.groups[1]?.value.orEmpty()
                val author = matchResult.groups[2]?.value.orEmpty()
                val date = matchResult.groups[3]?.value.orEmpty()
                val lineNumber = matchResult.groups[4]?.value?.toIntOrNull() ?: 0
                val content = matchResult.groups[5]?.value.orEmpty()

                // Fetch commit message for unique hashes
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
